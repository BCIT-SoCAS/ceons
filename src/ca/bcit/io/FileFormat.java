package ca.bcit.io;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class FileFormat<T> {

	public File file;

	public LightScanner getStream() {
		return new LightScanner(file);
	}

	public abstract String getExtension();

	public File getFile() {
		return file;
	}

	public abstract boolean loadWithData(T dataContainer);

	public static <T extends FileFormat<?>> T constructor(Class<T> fileFormatType, String path) throws FileNotFoundException, InvalidExtensionException {
		return constructor(fileFormatType, new File(path));
	}

	private static <T extends FileFormat<?>> T constructor(Class<T> fileFormatType, File file) throws FileNotFoundException, InvalidExtensionException {
		T fileFormat;

		try {
			fileFormat = fileFormatType.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
		if (!file.getAbsolutePath().endsWith("." + fileFormat.getExtension()))
			throw new InvalidExtensionException(file.getAbsolutePath(), fileFormat.getExtension());
		
		fileFormat.file = file;
		
		return fileFormat;
	}
}
