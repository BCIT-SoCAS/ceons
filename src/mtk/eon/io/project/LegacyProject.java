package mtk.eon.io.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;

import mtk.eon.io.legacy.DemandLoader;
import mtk.eon.jfx.tasks.SimulationTask;
import mtk.eon.net.Network;
import mtk.eon.net.demand.generator.TrafficGenerator;

public class LegacyProject extends Project {
	
	Network network;
	File demandDirectory;
	List<TrafficGenerator> generators =  new ArrayList<TrafficGenerator>();
	
	public LegacyProject(File projectFile, Network network, File demandDirectory) {
		super(projectFile);
		this.network = network;
		this.demandDirectory = demandDirectory;
	}
	
	@Override
	public Network getNetwork() {
		return network;
	}
	
	public DemandLoader getDefaultDemandLoader(SimulationTask task) throws NotDirectoryException, FileNotFoundException {
		return new DemandLoader(demandDirectory, network, task);
	}

	@Override
	public List<TrafficGenerator> getTrafficGenerators() {
		return generators;
	}
}
