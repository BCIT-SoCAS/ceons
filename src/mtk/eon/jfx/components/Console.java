package mtk.eon.jfx.components;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.scene.control.TextArea;
import mtk.eon.io.LightScanner;

public class Console extends TextArea {
	
	private static class ConsoleOutputStream extends OutputStream {

		Console console;
		
		public ConsoleOutputStream(Console console) {
			this.console = console;
		}
		
		@Override
		public void write(int character) throws IOException {
			console.appendCharacter((char) character);
			
		}
	}
	
	public final PrintStream out;
	public final LightScanner in;
	
	public Console() {
		super();
		out = new PrintStream(new ConsoleOutputStream(this));
		in = new LightScanner(file);
	}
	
	private void appendCharacter(char character) {
		appendText("" + character);
	}
}
