package mtk.eon.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LightScanner {
	
	static final int BUFFER_SIZE = 4096;
	BufferedInputStream in;
	
	public LightScanner(InputStream stream) {
		in = new BufferedInputStream(stream, BUFFER_SIZE);
	}
	
	public LightScanner(InputStream stream, int bufferSize) {
		in = new BufferedInputStream(stream, bufferSize);
	}
	
	public LightScanner(File file) {
		try {
			in = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not read file: " + file.getName(), e);
		}
	}
	
	public boolean isEOF() {
		try {
			char c;
			while (in.available() > 0) {
				in.mark(1);
				c = (char) in.read();
				in.reset();
				if (!Character.isWhitespace(c)) return false;
				in.read();
			}
			return true;
		} catch (IOException e) {
			throw new RuntimeException("Error while reading file...", e);
		}
	}
	
	public int nextInt() {
		try {
			int i = 0;
			char c;
			do c = (char) in.read(); while (!Character.isDigit(c));
			do {
				i *= 10;
				i += c - '0';
				c = (char) in.read();
			} while (Character.isDigit(c));
			return i;
		} catch (IOException e) {
			throw new RuntimeException("Error while reading integer from: " + in);
		}
	}
	
	public String nextLine() {
		return nextString(System.getProperty("line.separator"));
	}
	
	public String nextString(String delimiter) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			do {
				if (in.available() == 0) return stringBuilder.toString();
				stringBuilder.append((char) in.read());
			} while (!stringBuilder.substring(Math.max(0, stringBuilder.length() - delimiter.length())).equals(delimiter));
			return stringBuilder.substring(0, stringBuilder.length() - delimiter.length());
		} catch(IOException e) {
			throw new RuntimeException("Error while reading string from: " + in);
		}
	}
	
	public void skipInt() {
		try {
			while (!Character.isDigit(in.read()));
			while (Character.isDigit(in.read()));
		} catch (IOException e) {
			throw new RuntimeException("Error while skipping integer from: " + in);
		}
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
