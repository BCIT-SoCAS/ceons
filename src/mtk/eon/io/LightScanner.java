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
				e.printStackTrace();
				throw new RuntimeException("Error while reading file", e);
			}
			counter = 0;
		}
		return buffer[counter++];
	}
	
	public boolean isEOF() {
		for (int i = counter; i < length; i++)
			if (!Character.isWhitespace(buffer[i]))
				return false;
		try {
			if (in.available() > 0) {
				length = in.read(buffer, 0, BUFFER_SIZE);
				counter = 0;
				return isEOF();
			} else
				return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while reading file", e);
		}
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
		return nextString('\n');
	}
	
	public String nextString(char delimiter) {
		StringBuilder stringBuilder = new StringBuilder();
		char c;
		do {
			c = (char) read();
			if (c == delimiter) return stringBuilder.toString();
			stringBuilder.append(c);
		} while (!isEOF());
		return stringBuilder.toString();
	}
	
	public void skipInt() {
		while (!Character.isDigit(read()));
		while (Character.isDigit(read()));
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
