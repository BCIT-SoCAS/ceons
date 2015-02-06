package mtk.eon;


public class ApplicationResources {
	
	private static Project project;
	
	public static void setProject(Project project) {
		ApplicationResources.project = project;
	}
	
	public static Project getProject() {
		return project;
	}
}
