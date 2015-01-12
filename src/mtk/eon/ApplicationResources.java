package mtk.eon;

import javafx.stage.Stage;

public class ApplicationResources {

	static Stage primaryStage;
	private static Project project;
	
	public static Stage getStage() {
		return primaryStage;
	}
	
	public static void setProject(Project project) {
		ApplicationResources.project = project;
	}
	
	public static Project getProject() {
		return project;
	}
}
