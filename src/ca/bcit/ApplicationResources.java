package ca.bcit;

import ca.bcit.io.project.Project;

public class ApplicationResources {
	private static Project project;
	
	public static void setProject(Project project) {
		ApplicationResources.project = project;
	}
	
	public static Project getProject() {
		return project;
	}
}
