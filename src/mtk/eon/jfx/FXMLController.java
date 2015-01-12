package mtk.eon.jfx;

import java.io.File;
import java.io.FileNotFoundException;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import mtk.eon.ApplicationResources;
import mtk.eon.Project;
import mtk.eon.io.InvalidExtensionException;
import mtk.eon.io.Logger;

public class FXMLController {
	
	public void loadNetworkAction(ActionEvent e) {
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = fileChooser.showOpenDialog(ApplicationResources.primaryStage);
		
		if (file != null) {
			System.out.println("Loading project from: " + file.getAbsolutePath());
			Project project;
			try {
				project = new Project(file);
			} catch (FileNotFoundException | InvalidExtensionException exc) {
				Logger.debug(exc.toString());
				Logger.info("Failed to load project...");
				return;
			}
			ApplicationResources.project = project;
			Logger.info("Project loaded successfully.");
		}
	}
}
