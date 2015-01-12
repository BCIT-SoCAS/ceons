package mtk.eon.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LightScanner {

	FileInputStream in;
	static final int BUFFER_SIZE = 4096;
	byte[] buffer = new byte[BUFFER_SIZE];
	int counter = BUFFER_SIZE;
	int length;
	
	public LightScanner(File file) {
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not read file: " + file.getName(), e);
		}
	}
	
	private byte read() {
		if (counter == BUFFER_SIZE) {
			try {
				length = in.read(buffer, 0, BUFFER_SIZE);
			} catch (IOException e) {
				throw new RuntimeException("Error while reading file", e);
			}
			counter = 0;
		}
		return buffer[counter++];
	}
	
	public boolean isEOF() {
		if (length < BUFFER_SIZE && counter >= length) return true;
		try {
			if (counter >= BUFFER_SIZE && in.available() == 0) return true;
		} catch (IOException e) {
			throw new RuntimeException("Error while reading file...", e);
		}
		return false;
	}
	
	public int nextInt() {
		int i = 0;
		char c;
		do c = (char) read(); while (!Character.isDigit(c));
		do {
			i *= 10;
			i += c - '0';
			c = (char) read();
		} while (Character.isDigit(c));
		return i;
	}
	
	public String nextLine() {
		return nextString(System.getProperty("line.separator"));
	}
	
	public String nextString(String delimiter) {
		StringBuilder stringBuilder = new StringBuilder();
		do {
			stringBuilder.append((char) read());
			if (isEOF()) return stringBuilder.toString();
		} while (!stringBuilder.substring(Math.max(0, stringBuilder.length() - delimiter.length())).equals(delimiter));
		return stringBuilder.substring(0, stringBuilder.length() - delimiter.length());
	}
	
	public void skipInt() {
		while (!Character.isDigit(read()));
		while (Character.isDigit(read()));
	}
	
	public void skipLine() {
		
	}
	
	public void skipString(String delimiter) {
		StringBuffer buffer = new StringBuffer(delimiter.length());
//		for (int i = 0; i < delimiter.length() && !)
//		buffer.
		//TODO finish light scanner
	}
	
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			new RuntimeException(e);
		}
	}
}
