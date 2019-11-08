package ca.bcit.io.project;

import ca.bcit.io.FileFormat;
import javafx.stage.FileChooser.ExtensionFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ProjectFileFormat<L, S> extends FileFormat<Project, L, S> {
	private static final Map<ExtensionFilter, ProjectFileFormat<?, ?>> projectFileFormats = new HashMap<>();
	
	public static void registerFileFormat(ProjectFileFormat<?, ?> projectFileFormat) {
		if (!projectFileFormats.containsKey(projectFileFormat.getExtensionFilter()))
			projectFileFormats.put(projectFileFormat.getExtensionFilter(), projectFileFormat);
	}
	
	public static ProjectFileFormat<?, ?> getFileFormat(ExtensionFilter extensionFilter) {
		return projectFileFormats.get(extensionFilter);
	}
	
	public static List<ExtensionFilter> getExtensionFilters() {
		return new ArrayList<>(projectFileFormats.keySet());
	}
}
