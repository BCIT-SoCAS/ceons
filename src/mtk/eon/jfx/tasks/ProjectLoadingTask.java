package mtk.eon.jfx.tasks;

import java.io.File;
import java.io.FileNotFoundException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import mtk.eon.ApplicationResources;
import mtk.eon.Project;
import mtk.eon.io.InvalidExtensionException;
import mtk.eon.io.Logger;

public class ProjectLoadingTask extends Task<Void> {

	private final File file;
	
	public ProjectLoadingTask(File file) {
		this.file = file;
	}
	
	@Override
	protected Void call() throws Exception {
		if (file != null) {
			Logger.info("Loading project from: " + file.getAbsolutePath());
			try {
				Project project = new Project(file);
				Logger.info("Project loaded successfully.");
				Platform.runLater(() -> ApplicationResources.setProject(project));
			} catch (FileNotFoundException | InvalidExtensionException exc) {
				Logger.debug(exc.toString());
				Logger.info("Failed to load project...");
				return null;
			}
		}
		
		return null;
	}
}
