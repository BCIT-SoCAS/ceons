package mtk.eon.jfx;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import mtk.eon.ApplicationResources;
import mtk.eon.jfx.tasks.ProjectLoadingTask;

public class FXMLController {
	
	public void loadNetworkAction(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = fileChooser.showOpenDialog(ApplicationResources.getStage());
		
		ProjectLoadingTask task = new ProjectLoadingTask(file);
		Thread thread = new Thread(task);
		thread.start();
	}
}
