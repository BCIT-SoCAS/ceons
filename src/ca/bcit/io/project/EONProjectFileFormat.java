package ca.bcit.io.project;

import ca.bcit.io.YamlConfiguration;
import ca.bcit.io.create.NewTopology;
import ca.bcit.io.create.SavedNodeDetails;
import ca.bcit.net.Modulation;
import ca.bcit.net.ModulationInIfStatement;
import ca.bcit.net.Network;
import ca.bcit.net.NetworkLink;
import ca.bcit.net.demand.generator.TrafficGenerator;
import com.google.maps.ImageResult;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser.ExtensionFilter;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
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

		BufferedImage map = null;
		InputStream in = zip.getInputStream(zip.getEntry("map.png"));
		map = ImageIO.read(in);

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
	public void save(File file, Project data, ObservableList<SavedNodeDetails> tableList, ImageResult staticMap, String apiKey) throws IOException {
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

		ArrayList<String> nodeNumReplicas = new ArrayList<String>();
		ArrayList<String> nodeNumInternationals = new ArrayList<String>();
		HashMap<String, ArrayList<String>> toSeralizeNodeTypes = new HashMap<String, ArrayList<String>>();
		HashMap<ArrayList<String>, HashMap<String, Object>> toSerializeNodeLinks = new HashMap<ArrayList<String>, HashMap<String, Object>>();

		for(SavedNodeDetails nodeDetails : tableList){
			if(nodeDetails.getNodeType().equals("International")){
				nodeNumInternationals.add(nodeDetails.nodeNumToString());
			} else if(nodeDetails.getNodeType().equals("Data Center, International")){
				nodeNumReplicas.add(nodeDetails.nodeNumToString());
				nodeNumInternationals.add(nodeDetails.nodeNumToString());
			} else if(nodeDetails.getNodeType().equals("Data Center")){
				nodeNumReplicas.add(nodeDetails.nodeNumToString());
			}
			for(Map.Entry<ArrayList<String>, HashMap<String, Object>> entry : nodeDetails.getConnectedNodeLinkMap().entrySet()) {
				if(!toSerializeNodeLinks.containsKey(entry.getKey())){
					Boolean hasLink = !(entry.getKey().get(0).split("_").length == 1);
					if(hasLink) {
						int nodeANum = Integer.parseInt(entry.getKey().get(0).split("_")[1]);
						String nodeA = tableList.get(nodeANum-1).getLocation();
						int nodeBNum = Integer.parseInt(entry.getKey().get(1).split("_")[1]);
						String nodeB = tableList.get(nodeBNum-1).getLocation();
						int length = NewTopology.calDistance(nodeA, nodeB, apiKey);
						// ------------------------------------------------------------------------------------------------------------------------------------------
						System.out.println("Link: nodeANum=" + nodeANum + ", nodeA=" + nodeA + ", nodeBNum=" + nodeBNum + ", nodeB=" + nodeB + ", length=" + length);
						// ------------------------------------------------------------------------------------------------------------------------------------------
						HashMap<String, Object> link = new HashMap<String, Object>();
						link.put("length", length);
						link.put("class", NetworkLink.class.getName());
						toSerializeNodeLinks.put(entry.getKey(), link);
					}
				}
			}
		}

		System.out.println(toSerializeNodeLinks);

		toSeralizeNodeTypes.put("replicas", nodeNumReplicas);
		toSeralizeNodeTypes.put("international", nodeNumInternationals);
		topology.put("groups", toSeralizeNodeTypes);
		topology.put("links", toSerializeNodeLinks);
		topology.put("class", Network.class.getName());

		zip.putNextEntry(new ZipEntry("topology.yml"));
		topology.save(new OutputStreamWriter(zip));
		zip.closeEntry();

		//make the modulations.yml
		YamlConfiguration modulations = new YamlConfiguration();
		for (Modulation modulation : Modulation.values()) {
			modulations.put(modulation.toString(), new HashMap<String, Object>());
			modulations.put(modulation.toString() + ".distances", new ArrayList<Integer>());
			modulations.put(modulation.toString() + ".consumptions", new ArrayList<Integer>());
			ModulationInIfStatement modulationInIfStatement = new ModulationInIfStatement();
			for (int i = 0; i < 40; i++) {
				modulations.put(modulation + ".distances." + i, modulationInIfStatement.modulationDistance(modulation.toString())[i]);
				modulations.put(modulation + ".consumptions." + i, modulationInIfStatement.slicesConsumption(modulation.toString())[i]);
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
