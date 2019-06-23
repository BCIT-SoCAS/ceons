package ca.bcit.io.project;

import ca.bcit.io.YamlConfiguration;
import ca.bcit.io.create.SavedNodeDetails;
import ca.bcit.net.Modulation;
import ca.bcit.net.Network;
import ca.bcit.net.demand.generator.TrafficGenerator;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser.ExtensionFilter;

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
public class EONProjectFileFormat extends ProjectFileFormat<Void, Void> {

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("EON project files", "*.eon");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Project load(File file, Void parameter) throws IOException {
		ZipFile zip = new ZipFile(file);
		YamlConfiguration projectConfig = new YamlConfiguration(zip.getInputStream(zip.getEntry("project.yml")));

		YamlConfiguration topology = new YamlConfiguration(zip.getInputStream(zip.getEntry(projectConfig.get("topology"))));
		Network network = topology.get("");
		System.out.println(network);

		String map = projectConfig.get("map");

		YamlConfiguration modulations = new YamlConfiguration(zip.getInputStream(zip.getEntry(projectConfig.get("modulations"))));
		for (Modulation modulation : Modulation.values())
			for (int i = 0; i < 40; i++) {
				modulation.modulationDistances[i] = modulations.get(modulation + ".distances." + i);
				modulation.slicesConsumption[i] = modulations.get(modulation + ".consumptions." + i);
			}

		List<TrafficGenerator> trafficGenerators = new ArrayList<>();
		for (String generatorFileName : (List<String>) projectConfig.get("generators"))
			trafficGenerators.add(new YamlConfiguration(zip.getInputStream(zip.getEntry(generatorFileName))).get(""));

		zip.close();
		return new EONProject(file, network, new ArrayList<>(), map);
	}

	@Override
	public void save(File file, Project data, ObservableList<SavedNodeDetails> tableList) throws IOException {
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));

		//make the project.yml
		/**
		YamlConfiguration projectConfig = new YamlConfiguration();
		projectConfig.put("topology", "topology.yml");
		projectConfig.put("modulations", "modulations.yml");
		//Not sure why these are here, may need to get rid of this later on
		List<String> generatorsNames = new ArrayList<>();
		//Not sure why these are placed in the list
//		for (TrafficGenerator generator : data.getTrafficGenerators()) generatorsNames.add(generator.getName() + ".yml");
		projectConfig.put("generators", generatorsNames);
		projectConfig.put("map", "");
		zip.putNextEntry(new ZipEntry("project.yml"));
		projectConfig.save(new OutputStreamWriter(zip));
		zip.closeEntry();
		 **/

		//make the topology.yml
		YamlConfiguration topology = new YamlConfiguration();
//		topology.put("", data.getNetwork());
		topology.put("nodes", tableList);
//		topology.put("groups", tableList.get(0));
//		topology.put("links", tableList.get(1));


		zip.putNextEntry(new ZipEntry("topology.yml"));
		topology.save(new OutputStreamWriter(zip));
		zip.closeEntry();

		//make the modulations.yml
		/**
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

//		for (TrafficGenerator generator : data.getTrafficGenerators()) {
//			YamlConfiguration generatorConfig = new YamlConfiguration();
//			generatorConfig.put("", generator);
//			zip.putNextEntry(new ZipEntry(generator.getName() + ".yml"));
//			generatorConfig.save(new OutputStreamWriter(zip));
//			zip.closeEntry();
//		}
		 **/

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
