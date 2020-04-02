package ca.bcit.io.project;

import ca.bcit.Settings;
import ca.bcit.io.YamlConfiguration;
import ca.bcit.io.create.SavedNodeDetails;
import ca.bcit.io.create.SavedNodeLinks;
import ca.bcit.io.create.SavedNodeTypes;
import ca.bcit.net.Network;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.modulation.IModulation;
import ca.bcit.utils.LocaleUtils;
import com.google.maps.ImageResult;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class EONProjectFileFormat extends ProjectFileFormat<Void, Void> {
	public EONProjectFileFormat() {
		super();
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter(LocaleUtils.translate("eon_project_files"), "*.eon");
	}

	@Override
	public Project load(File file, Void parameter) throws IOException {
		ZipFile zip = new ZipFile(file);
		YamlConfiguration projectConfig = new YamlConfiguration(zip.getInputStream(zip.getEntry("project.yml")));

		YamlConfiguration topology = new YamlConfiguration(zip.getInputStream(zip.getEntry(projectConfig.get("topology"))));
		Network network = topology.get("");

		BufferedImage map;
		InputStream in = zip.getInputStream(zip.getEntry("map.png"));
		map = ImageIO.read(in);

		YamlConfiguration modulations = new YamlConfiguration(zip.getInputStream(zip.getEntry(projectConfig.get("modulations"))));
		for (IModulation modulation : Settings.registeredModulations.values())
			for (int i = 0; i < 40; i++) {
				modulation.getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps()[i] = modulations.get(modulation.getKey() + ".distances." + i);
				modulation.getSlicesConsumptionByBitrateWithJumpsOfTenGbps()[i] = modulations.get(modulation.getKey() + ".consumptions." + i);
			}

		List<TrafficGenerator> trafficGenerators = new ArrayList<>();
		for (String generatorFileName : (List<String>) projectConfig.get("generators"))
			trafficGenerators.add(new YamlConfiguration(zip.getInputStream(zip.getEntry(generatorFileName))).get(""));

		zip.close();
		return new EONProject(file, network, new ArrayList<>(), map);
	}

	@Override
	public void save(File file, Project data, ObservableList<SavedNodeDetails> tableList, ImageResult staticMap) throws IOException {
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));

		//make the project.yml

		YamlConfiguration projectConfig = new YamlConfiguration();
		projectConfig.put("topology", "topology.yml");
		projectConfig.put("modulations", "modulations.yml");
		//Not sure why these are here, may need to get rid of this later on
		List<String> generatorsNames = new ArrayList<>();
		//Not sure why these are placed in the list
		//for (TrafficGenerator generator : data.getTrafficGenerators()) generatorsNames.add(generator.getName() + ".yml");
		projectConfig.put("generators", generatorsNames);
		zip.putNextEntry(new ZipEntry("project.yml"));
		projectConfig.save(new OutputStreamWriter(zip));
		zip.closeEntry();

		//save static map to zip
		ZipEntry map = new ZipEntry("map.png");
		zip.putNextEntry(map);
		byte[] imgData = staticMap.imageData;
		zip.write(imgData, 0, imgData.length);
		zip.closeEntry();

		//make the topology.yml
		YamlConfiguration topology = new YamlConfiguration();
		topology.put("nodes", tableList);

		SavedNodeTypes savedNodeTypes = new SavedNodeTypes();
		SavedNodeLinks savedNodeLinks = new SavedNodeLinks();

		for (SavedNodeDetails nodeDetails : tableList) {
			savedNodeTypes.setNodeNumType(nodeDetails);
			savedNodeLinks.setNodeNumLinks(nodeDetails, tableList);
		}

		topology.put("groups", savedNodeTypes.getToSerializeNodeTypes());
		topology.put("links", savedNodeLinks.getToSerializeNodeLinks());
		topology.put("class", Network.class.getName());

		zip.putNextEntry(new ZipEntry("topology.yml"));
		topology.save(new OutputStreamWriter(zip));
		zip.closeEntry();

		//make the modulations.yml
		YamlConfiguration modulations = new YamlConfiguration();
		for (IModulation modulation : Settings.registeredModulations.values()) {
			modulations.put(modulation.getKey(), new HashMap<String, Object>());
			modulations.put(modulation.getKey() + ".distances", new ArrayList<Integer>());
			modulations.put(modulation.getKey() + ".consumptions", new ArrayList<Integer>());

			for (int i = 0; i < 40; i++) {
				modulations.put(modulation.getKey() + ".distances." + i, modulation.getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps()[i]);
				modulations.put(modulation.getKey() + ".consumptions." + i, modulation.getSlicesConsumptionByBitrateWithJumpsOfTenGbps()[i]);
			}
		}
		zip.putNextEntry(new ZipEntry("modulations.yml"));
		modulations.save(new OutputStreamWriter(zip));
		zip.closeEntry();

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
