package mtk.eon.io;

import java.io.File;
import java.io.IOException;

import javafx.stage.FileChooser.ExtensionFilter;

public abstract class FileFormat2<D, L, S> {
	
	public abstract ExtensionFilter getExtensionFilter();
	
	public abstract D load(File file, L parameter) throws IOException;
	
	public D load(File file) throws IOException {
		if (hasLoadParameter()) throw new FileFormatException("Loading paramter missing!");
		return load(file, null);
	}
	
	public abstract void save(File file, D data, S parameter) throws IOException;

	public void save(File file, D data) throws IOException {
		if (hasSaveParameter()) throw new FileFormatException("Saving paramter missing!");
		save(file, data, null);
	}
	
	public abstract boolean hasLoadParameter();
	
	public abstract boolean hasSaveParameter();
}
