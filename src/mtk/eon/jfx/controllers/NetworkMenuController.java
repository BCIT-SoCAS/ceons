package mtk.eon.jfx.controllers;

import java.io.File;
import java.util.zip.ZipFile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import mtk.eon.io.Logger;
import mtk.eon.io.project.EONProjectFileFormat;
import mtk.eon.jfx.tasks.ProjectLoadingTask;

public class NetworkMenuController {

	@FXML public void onNew(ActionEvent e) {
		Logger.debug("new");
	}
	
	@FXML public void onLoad(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("EON project file", "eon"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = fileChooser.showOpenDialog(null);
		
		try {
			EONProjectFileFormat pf = new EONProjectFileFormat(new ZipFile(file));
			pf.load(null);
		} catch (Exception ex) {
			Logger.debug(ex.getMessage());
		}
	}
	
	@FXML public void onSave(ActionEvent e) {
		Logger.debug("save");
	}
	
	@FXML public void loadNetworkAction(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		File file = fileChooser.showOpenDialog(null);
		
		ProjectLoadingTask task = new ProjectLoadingTask(file);
		Thread thread = new Thread(task);
		thread.start();
	}

	int i;
	@FXML public void testButton(ActionEvent e) {
//		Task<Void> task = new Task<Void>() {
//
//			@Override
//			protected Void call() throws Exception {
//				Network network = ApplicationResources.getProject().getNetwork();
//				
//				for (NetworkNode node : network.getNodes()) {
//					Console.cout.println(node.getName() + " - " + node.getID() + " - " + (node.isReplica() ? "Is replica" : "Isn't replica") + " - Free regs: " + node.getFreeRegenerators());
//				}
//				i = 1;
//				int p = 0;
//				try {
//					p = network.calculatePaths(() -> updateProgress(i++, network.getNodesPairsCount()));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				Console.cout.println("Max best paths count: " + p);
//				
//				return null;
//			}
//			
//			
//		};
//		progressBar.runTask(task, true);
	}
}
