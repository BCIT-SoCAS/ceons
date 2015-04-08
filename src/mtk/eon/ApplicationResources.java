package mtk.eon;

import mtk.eon.io.project.LegacyProject;


public class ApplicationResources {
	
	private static LegacyProject project;
	
	public static void setProject(LegacyProject project) {
		ApplicationResources.project = project;
	}
	
	public static LegacyProject getProject() {
		return project;
	}
}
