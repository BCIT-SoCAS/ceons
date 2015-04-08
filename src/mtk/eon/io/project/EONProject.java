package mtk.eon.io.project;

import java.io.File;
import java.util.List;

import mtk.eon.net.Network;
import mtk.eon.net.demand.generator.TrafficGenerator;

public class EONProject extends Project {

	private Network network;
	private List<TrafficGenerator> trafficGenerators;
	
	public EONProject(File projectFile, Network network, List<TrafficGenerator> trafficGenerators) {
		super(projectFile);
		this.network = network;
		this.trafficGenerators = trafficGenerators;
	}

	@Override
	public Network getNetwork() {
		return network;
	}

	@Override
	public List<TrafficGenerator> getTrafficGenerators() {
		return trafficGenerators;
	}
}
