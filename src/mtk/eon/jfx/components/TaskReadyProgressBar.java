package mtk.eon.jfx.components;

import mtk.eon.io.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

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
		bar.progressProperty().bind(task.progressProperty());
		label.textProperty().bind(task.messageProperty());
	}
	
	private void unbind() {
		bar.setVisible(false);
		bar.progressProperty().unbind();
		label.textProperty().unbind();
	}
	
	public void runTask(Task<?> task, boolean daemon) {
		bind(task);
		task.setOnSucceeded(e -> onSucceeded(e));
		task.setOnFailed(e -> onFailed(e));
		task.setOnCancelled(e -> onCancelled(e));
		Thread thread = new Thread(task);
		thread.setDaemon(daemon);
		thread.start();
	}
	
	private void onSucceeded(WorkerStateEvent e) {
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