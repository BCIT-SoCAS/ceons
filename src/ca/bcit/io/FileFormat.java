package ca.bcit.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import ca.bcit.io.create.SavedNodeDetails;
import com.google.maps.ImageResult;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser.ExtensionFilter;

public abstract class FileFormat<D, L, S> {

	public abstract ExtensionFilter getExtensionFilter();

	public abstract D load(File file, L parameter) throws IOException;

	public D load(File file) throws IOException {
		if (hasLoadParameter()) throw new FileFormatException("Loading parameter missing!");
		return load(file, null);
	}

//	public abstract void save(File file, D data, S parameter) throws IOException;
//
//	public void save(File file, D data) throws IOException {
//		if (hasSaveParameter()) throw new FileFormatException("Saving paramter missing!");
//		save(file, data, null);
//	}

	public abstract void save(File file, D data, ObservableList<SavedNodeDetails> tableList, ImageResult staticMap, String apiKey) throws IOException;

	public abstract boolean hasLoadParameter();

	public abstract boolean hasSaveParameter();
}