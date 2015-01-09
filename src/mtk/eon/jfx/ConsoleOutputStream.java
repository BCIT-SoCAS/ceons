package mtk.eon.jfx;

import java.io.IOException;
import java.io.OutputStream;

import javafx.scene.control.TextArea;

public class ConsoleOutputStream extends OutputStream {

	TextArea console;
	
	public ConsoleOutputStream(TextArea console) {
		super();
		this.console = console;
	}

	@Override
	public void write(int b) throws IOException {
		console.appendText(Character.toString((char) b));
	}
}
