package ca.bcit.jfx.components;

import ca.bcit.io.Logger;
import ca.bcit.jfx.controllers.SimulationMenuController;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TaskReadyProgressBar extends StackPane {

	private final ProgressBar bar = new ProgressBar();
	private final Label label = new Label("");
	
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
			onSucceeded();
		});
		task.setOnFailed(this::onFailed);
		task.setOnCancelled(this::onCancelled);
		Thread thread = new Thread(task);
		thread.setDaemon(daemon);
		thread.start();
	}

	public void onSucceeded() {
		unbind();
	}

	private void onFailed(WorkerStateEvent e) {
		Logger.debug(e.getSource().toString() + " failed!");
		unbind();
	}
	
	private void onCancelled(WorkerStateEvent e) {
		Logger.debug(e.getSource().toString() + " was cancelled!");
		unbind();
	}
}