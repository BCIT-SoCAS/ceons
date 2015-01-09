package mtk.eon.io;

import java.io.File;
import java.nio.file.NoSuchFileException;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.media.sound.InvalidFormatException;

public abstract class FileFormat<T> {

	File file;
	
	public LightScanner getStream() {
		return new LightScanner(file);
	}
	
	public abstract String getExtension();
	
	public File getFile() {
		return file;
	}
	
	public abstract boolean loadWithData(T dataContainer);
	
	public static <T extends FileFormat<?>> T constructor(Class<T> fileFormatType, String path) throws NoSuchFileException, InvalidExtensionException {
		return constructor(fileFormatType, new File(path));
	}
	
	public static <T extends FileFormat<?>> T constructor(Class<T> fileFormatType, File file) throws NoSuchFileException, InvalidExtensionException {
		T fileFormat;
		
		try {
			fileFormat = fileFormatType.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if (!file.exists()) throw new NoSuchFileException(file.getAbsolutePath());
		if (!file.getAbsolutePath().endsWith("." + fileFormat.getExtension())) throw new InvalidExtensionException(file.getAbsolutePath(), fileFormat.getExtension());
		
		fileFormat.file = file;
		
		return fileFormat;
	}
}
