package mtk.eon.jfx;

import java.io.File;
import java.nio.file.NoSuchFileException;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import javax.swing.JOptionPane;

import mtk.eon.ApplicationResources;
import mtk.eon.Project;
import mtk.eon.io.InvalidExtensionException;

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
			} catch (NoSuchFileException | InvalidExtensionException exc) {
				JOptionPane.showMessageDialog(null, exc.getMessage());
				return;
			}
			ApplicationResources.project = project;
		}
	}
}
