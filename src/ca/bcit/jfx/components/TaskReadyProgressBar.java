package ca.bcit.jfx.components;

import ca.bcit.io.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

import java.util.concurrent.*;

public class TaskReadyProgressBar extends StackPane {
	private final ProgressBar bar = new ProgressBar();
	private final Label label = new Label("");
	private ExecutorService runMultipleSimulationService;
	private int numSimulationsLeft = 0;
	
	public TaskReadyProgressBar() {
		super();
		getChildren().add(bar);
		getChildren().add(label);
		bar.setVisible(false);
		bar.minWidthProperty().bind(widthProperty());
		bar.minHeightProperty().bind(heightProperty());
	}
	
	private void bind(Task<?> task) {
		bar.setVisible(true);
		bar.progressProperty().bind(task.progressProperty()); // possible docking location for pause
		label.textProperty().bind(task.messageProperty());
	}
	
	private void unbind() {
		bar.setVisible(false);
		bar.progressProperty().unbind();
		label.textProperty().unbind();
	}
	
	public void runTask(Task<?> task, boolean daemon) {
		bind(task);
		task.setOnSucceeded(e -> {
			unbind();
		});
		task.setOnFailed(e -> {
			unbind();
			Logger.debug(e.getSource().toString() + " failed!");
		});
		task.setOnCancelled(e -> {
			unbind();
			Logger.debug(e.getSource().toString() + " was cancelled!");
		});;
		Thread thread = new Thread(task);
		thread.setDaemon(daemon);
		thread.start();
	}

	public void runTask(Task<?> task, boolean daemon, ExecutorService runMultipleSimulationService) {
		bind(task);
		setRunMultipleSimulationService(runMultipleSimulationService);
		task.setOnSucceeded(e -> {
			unbind();
			numSimulationsLeft--;
			if(numSimulationsLeft == 0){
				runMultipleSimulationService.shutdown();
				try {
					if (!runMultipleSimulationService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
						runMultipleSimulationService.shutdownNow();
					}
				} catch (InterruptedException ex) {
					runMultipleSimulationService.shutdownNow();
				}
			}
		});
		task.setOnFailed(e -> {
			unbind();
			Logger.debug(e.getSource().toString() + " failed!");
		});
		task.setOnCancelled(e -> {
			unbind();
			Logger.debug(e.getSource().toString() + " was cancelled!");
		});

		//Start the thread task execution
		Thread thread = new Thread(task);
		thread.setDaemon(daemon);
		runMultipleSimulationService.execute(thread);
	}

	public void setRunMultipleSimulationService(ExecutorService runMultipleSimulationService) {
			this.runMultipleSimulationService = runMultipleSimulationService;
	}

	public ExecutorService getRunMultipleSimulationService() {
		return runMultipleSimulationService;
	}

	public void increaseSimulationCount(){
		numSimulationsLeft++;
	}

}