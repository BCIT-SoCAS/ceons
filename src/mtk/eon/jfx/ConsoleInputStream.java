package mtk.eon.jfx;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class ConsoleInputStream extends InputStream {
	
	LinkedList<Byte> buffer = new LinkedList<Byte>();
	boolean isWaiting;
	boolean isWriting;

	public ConsoleInputStream(TextField textField) {
		textField.setOnKeyPressed(evt -> {
			if (evt.getCode() != KeyCode.ENTER) return;
			if (!isWaiting) { textField.setText(""); }
			isWriting = true;
			byte[] data = textField.getText().getBytes();
			textField.setText("");
			for (Byte b : data) buffer.offer(b);
			buffer.offer((byte) 13);
			buffer.offer((byte) 10);
			isWriting = false;
		});
	}
	
	public void waitForInput() {
		while(buffer.isEmpty() || isWriting) {
			isWaiting = true;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isWaiting = false;
	}
	
	@Override
	public int read() throws IOException {
		waitForInput();
		int result = buffer.poll().intValue() & 0xFF;
		return result;
	}
	
	@Override
	public int read(byte[] bytes) {
		waitForInput();
		int i;
		for (i = 0; i < bytes.length && !buffer.isEmpty(); i++)
			bytes[i] = buffer.poll();
		return i;
	}
	
	@Override
	public int read(byte[] bytes, int offset, int length) {
		waitForInput();
		int i;
		for (i = 0; i < length && !buffer.isEmpty(); i++)
			bytes[offset + i] = buffer.poll();
		return i;
	}
}
