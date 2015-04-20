package mtk.eon.io.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.stage.FileChooser.ExtensionFilter;
import mtk.eon.io.FileFormat2;

public abstract class ProjectFileFormat<L, S> extends FileFormat2<Project, L, S> {

	private static Map<ExtensionFilter, ProjectFileFormat<?, ?>> projectFileFormats = new HashMap<ExtensionFilter, ProjectFileFormat<?,?>>();
	
	public static void registerFileFormat(ProjectFileFormat<?, ?> projectFileFormat) {
		if (!projectFileFormats.containsKey(projectFileFormat.getExtensionFilter()))
			projectFileFormats.put(projectFileFormat.getExtensionFilter(), projectFileFormat);
	}
	
	public static ProjectFileFormat<?, ?> getFileFormat(ExtensionFilter extensionFilter) {
		return projectFileFormats.get(extensionFilter);
	}
	
	public static List<ExtensionFilter> getExtensionFilters() {
		return new ArrayList<ExtensionFilter>(projectFileFormats.keySet());
	}
}
