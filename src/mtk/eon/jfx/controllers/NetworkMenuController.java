package mtk.eon.jfx.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import mtk.eon.ApplicationResources;
import mtk.eon.jfx.components.Console;
import mtk.eon.net.Network;
import mtk.eon.net.NetworkNode;

public class NetworkMenuController {

	@FXML public void loadNetworkAction(ActionEvent e) {
//		FileChooser fileChooser = new FileChooser();
//		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
//		File file = fileChooser.showOpenDialog(.getScene().getWindow());
//		
//		ProjectLoadingTask task = new ProjectLoadingTask(file);
//		Thread thread = new Thread(task);
//		thread.start();
	}

	int i;
	@FXML public void testButton(ActionEvent e) {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				Network network = ApplicationResources.getProject().getNetwork();
				
				for (NetworkNode node : network.getNodes()) {
					Console.cout.println(node.getName() + " - " + node.getID() + " - " + (node.isReplica() ? "Is replica" : "Isn't replica") + " - Free regs: " + node.getFreeRegenerators());
				}
				i = 1;
				int p = 0;
				try {
					p = network.calculatePaths(() -> updateProgress(i++, network.relationsSize()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Console.cout.println("Max best paths count: " + p);
				
				return null;
			}
			
			
		};
//		progressBar.runTask(task, true);
	}
}
