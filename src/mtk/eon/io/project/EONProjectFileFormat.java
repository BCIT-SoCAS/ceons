package mtk.eon.io.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javafx.stage.FileChooser.ExtensionFilter;
import mtk.eon.io.YamlConfiguration;
import mtk.eon.net.Modulation;
import mtk.eon.net.Network;
import mtk.eon.net.demand.generator.TrafficGenerator;

public class EONProjectFileFormat extends ProjectFileFormat<Void, Void> {

	@SuppressWarnings("restriction")
	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("EON project files", "*.eon");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Project load(File file, Void parameter) throws IOException {
		ZipFile zip = new ZipFile(file);
		YamlConfiguration projectConfig = new YamlConfiguration(zip.getInputStream(zip.getEntry("project.yml")));
		
		YamlConfiguration topology = new YamlConfiguration(zip.getInputStream(zip.getEntry(projectConfig.get(String.class, "topology"))));
		Network network = topology.get(Network.class, "");
		
		YamlConfiguration modulations = new YamlConfiguration(zip.getInputStream(zip.getEntry(projectConfig.get(String.class, "modulations"))));
		for (Modulation modulation : Modulation.values())
			for (int i = 0; i < 40; i++) {
				modulation.modulationDistances[i] = modulations.get(Integer.class, modulation + ".distances." + i);
				modulation.slicesConsumption[i] = modulations.get(Integer.class, modulation + ".consumptions." + i);
			}
		
		List<TrafficGenerator> trafficGenerators = new ArrayList<TrafficGenerator>();
		for (String generatorFileName : (List<String>) projectConfig.get(List.class, "generators"))
			trafficGenerators.add(new YamlConfiguration(zip.getInputStream(zip.getEntry(generatorFileName))).get(TrafficGenerator.class, ""));
		
		zip.close();
		return new EONProject(file, network, new ArrayList<TrafficGenerator>());
	}

	@Override
	public void save(File file, Project data, Void parameter) throws IOException {
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));
		
		YamlConfiguration projectConfig = new YamlConfiguration();
		projectConfig.put("topology", "topology.yml");
		projectConfig.put("modulations", "modulations.yml");
		List<String> generatorsNames = new ArrayList<String>();
		for (TrafficGenerator generator : data.getTrafficGenerators()) generatorsNames.add(generator.getName() + ".yml");
		projectConfig.put("generators", generatorsNames);
		zip.putNextEntry(new ZipEntry("project.yml"));
		projectConfig.save(new OutputStreamWriter(zip));
		zip.closeEntry();
		
		YamlConfiguration topology = new YamlConfiguration();
		topology.put("", data.getNetwork());
		zip.putNextEntry(new ZipEntry("topology.yml"));
		topology.save(new OutputStreamWriter(zip));
		zip.closeEntry();
		
		YamlConfiguration modulations = new YamlConfiguration();
		for (Modulation modulation : Modulation.values()) {
			modulations.put(modulation.toString(), new HashMap<String, Object>());
			modulations.put(modulation.toString() + ".distances", new ArrayList<Integer>());
			modulations.put(modulation.toString() + ".consumptions", new ArrayList<Integer>());
			for (int i = 0; i < 40; i++) {
				modulations.put(modulation + ".distances." + i, modulation.modulationDistances[i]);
				modulations.put(modulation + ".consumptions." + i, modulation.slicesConsumption[i]);
			}
		}
		zip.putNextEntry(new ZipEntry("modulations.yml"));
		modulations.save(new OutputStreamWriter(zip));
		zip.closeEntry();
		
		for (TrafficGenerator generator : data.getTrafficGenerators()) {
			YamlConfiguration generatorConfig = new YamlConfiguration();
			generatorConfig.put("", generator);
			zip.putNextEntry(new ZipEntry(generator.getName() + ".yml"));
			generatorConfig.save(new OutputStreamWriter(zip));
			zip.closeEntry();
		}
		
		zip.close();
	}

	@Override
	public boolean hasLoadParameter() {
		return false;
	}

	@Override
	public boolean hasSaveParameter() {
		return false;
	}
}
