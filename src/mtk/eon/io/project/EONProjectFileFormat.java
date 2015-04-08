package mtk.eon.io.project;

import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import mtk.eon.io.FileFormat2;
import mtk.eon.io.YamlConfiguration;

public class EONProjectFileFormat implements FileFormat2<LegacyProject, Void> {

	ZipFile file;

	public EONProjectFileFormat(String path) throws ZipException, IOException {
		this(new ZipFile(path));
	}

	public EONProjectFileFormat(ZipFile file) throws IOException {
		this.file = file;
		if (this.file.getEntry("project.yml") == null) throw new IOException("Project at " +
				file.getName() + " is critically damaged and cannot be read.");
	}

	@Override
	public String getPreferredExtension() {
		return ".eon";
	}

	@Override
	public LegacyProject load(Void parameter) throws IOException {
		YamlConfiguration projectConfig = new YamlConfiguration(file.getInputStream(file.getEntry("project.yml")));
		return null;
	}
}
