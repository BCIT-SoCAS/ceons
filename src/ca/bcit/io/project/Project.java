package ca.bcit.io.project;

import ca.bcit.net.Network;
import ca.bcit.net.demand.generator.TrafficGenerator;

import java.io.File;
import java.util.List;

public abstract class Project {

	private final File projectFile;
	
	Project(File projectFile) {
		this.projectFile = projectFile;
	}
	
	public String getName() {
		return projectFile.getName().replaceFirst("\\.[^.]*$", "");
	}
	
	public abstract Network getNetwork();
	
	public abstract List<TrafficGenerator> getTrafficGenerators();

	public abstract String getMap();
}
