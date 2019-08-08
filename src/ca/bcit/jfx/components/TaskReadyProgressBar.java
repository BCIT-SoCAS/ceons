package ca.bcit.jfx.components;

import ca.bcit.io.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TaskReadyProgressBar extends StackPane {
	private final ExecutorService runMultipleSimulationService = Executors.newSingleThreadExecutor(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = Executors.defaultThreadFactory().newThread(runnable);
			thread.setDaemon(true);
			return thread;
		}
	});
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
		runMultipleSimulationService.execute(thread);
//		thread.start();
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

	public ExecutorService getRunMultipleSimulationService(){
		return runMultipleSimulationService;
	}
}