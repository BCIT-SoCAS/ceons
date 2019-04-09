package ca.bcit.io.project;

import ca.bcit.net.Network;
import ca.bcit.net.demand.generator.TrafficGenerator;

import java.io.File;
import java.util.List;

public class EONProject extends Project {

	private final Network network;
	private final List<TrafficGenerator> trafficGenerators;
	
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
