package mtk.eon.jfx.tasks;

import java.io.File;

import javafx.application.Platform;
import javafx.concurrent.Task;
import mtk.eon.ApplicationResources;
import mtk.eon.io.Logger;
import mtk.eon.io.project.LegacyProject;

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
				LegacyProject project = new LegacyProject(file);
				Logger.info("Project loaded successfully.");
				Platform.runLater(() -> ApplicationResources.setProject(project));
			} catch (Exception e) {
				Logger.debug(e.toString());
				Logger.info("Failed to load project...");
				return null;
			}
		}
		
		return null;
	}
}
