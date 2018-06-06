package ca.bcit.io;

import java.nio.file.FileSystemException;

class InvalidExtensionException extends FileSystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9121773758494735679L;

	public InvalidExtensionException(String file, String expectedExtension) {
		super(file, null, "Invalid extension! Expected: \"" + expectedExtension + "\"");
	}

}
