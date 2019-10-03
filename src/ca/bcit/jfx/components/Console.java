package ca.bcit.jfx.components;

import ca.bcit.io.LightScanner;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class Console extends TextArea {
	
	public static final PrintStream cout = null;
	public static final LightScanner cin = null;

	private static class ConsoleOutputStream extends OutputStream {

		final Console console;
		
		ConsoleOutputStream(Console console) {
			this.console = console;
		}
		
		@Override
		public void write(int character) {
			if (Platform.isFxApplicationThread()) console.appendCharacter((char) character);
			else Platform.runLater(() -> console.appendCharacter((char) character));
		}
	}
	
	private static class ConsoleInputStream extends InputStream {
		final Console console;
		Object monitor;
		final StringBuffer buffer = new StringBuffer();
		
		ConsoleInputStream(Console console) {
			this.console = console;
		}
		
		void appendCharacter(char character) {
			buffer.append(character);
		}
		
		void deleteCharacter() {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		
		private void requestInput() throws IOException {
			if (monitor != null)
				throw new IOException("Tried to read from console on separate threads at the same time.");

			monitor = new Object();

			if (Platform.isFxApplicationThread())
				console.setEditable(true);
			else
				Platform.runLater(() -> console.setEditable(true));

			synchronized(monitor) {
				try {
					monitor.wait();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (Platform.isFxApplicationThread())
				console.setEditable(false);
			else
				Platform.runLater(() -> console.setEditable(false));
			monitor = null;
		}
		
		@Override
		public int read() throws IOException {
			if (buffer.length() == 0)
				requestInput();
			int result = buffer.charAt(0);
			buffer.deleteCharAt(0);
			return result;
		}
		
		@Override
		public int read(byte[] bytes) {
			if (buffer.length() == 0)
				try {
					requestInput();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			int i;
			for (i = 0; i < bytes.length && buffer.length() != 0; i++)
				try {
					bytes[i] = (byte) read();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			return i;
		}
		
		@Override
		public int read(byte[] bytes, int offset, int length) {
			if (buffer.length() == 0)
				try {
					requestInput();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			int i;
			for (i = 0; i < length && buffer.length() != 0; i++)
				try {
					bytes[offset + i] = (byte) read();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			return i;
		}
	}
	
	private final ConsoleInputStream inputStream;
	public final PrintStream out;
	public final LightScanner in;
	
	public Console() {
		super();
		ConsoleOutputStream outputStream = new ConsoleOutputStream(this);
		inputStream = new ConsoleInputStream(this);
		out = new PrintStream(outputStream);
		in = new LightScanner(inputStream);
		setEditable(false);
		addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPress);
	}
	
	private void appendCharacter(char character) {
		appendText("" + character);
	}
	
	private void onKeyPress(KeyEvent e) {
		if (!isEditable()) return;
		
		switch(e.getCode()) {
			case BACK_SPACE:
				if (inputStream.buffer.length() <= 0)
					e.consume();
				else
					inputStream.deleteCharacter();
				break;
			case ENTER:
				inputStream.appendCharacter('\n');
				synchronized (inputStream.monitor) {
					inputStream.monitor.notify();
				}
				break;
			default:
				if (e.getText().length() > 0)
					inputStream.appendCharacter(e.getText().charAt(0));
				else
					e.consume();
		}
	}
}
