package mtk.eon.io.project;

import java.io.File;
import java.util.List;

import mtk.eon.net.Network;
import mtk.eon.net.demand.generator.TrafficGenerator;

public abstract class Project {

	private File projectFile;
	
	public Project(File projectFile) {
		this.projectFile = projectFile;
	}
	
	public File getProjectFile() {
		return projectFile;
	}
	
	public String getName() {
		return projectFile.getName().replaceFirst("\\.[^.]*$", "");
	}
	
	public abstract Network getNetwork();
	
	public abstract List<TrafficGenerator> getTrafficGenerators();
}
