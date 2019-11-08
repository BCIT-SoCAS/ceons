package ca.bcit.io;

import java.io.*;

public class LightScanner {
	private static final int BUFFER_SIZE = 4096;
	private BufferedInputStream in;

	public LightScanner(InputStream stream) {
		in = new BufferedInputStream(stream, BUFFER_SIZE);
	}
}
