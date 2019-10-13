package ca.bcit.jfx.components;

import javafx.scene.control.TextField;

public class UIntField extends TextField {
	
	public UIntField() {
		super("0");
	}
	
	public UIntField(int uint) {
		super(String.valueOf(uint));
		if (uint < 0)
			throw new IllegalArgumentException("UIntField cannot be initialized with negative value: " + uint + ".");
	}
	
	@Override
	public void replaceText(int start, int end, String text) {
		if (text.matches("[0-9]*")) super.replaceText(start, end, text);
		correctText();
	}
	
	@Override
	public void replaceSelection(String text) {
		if (text.matches("[0-9]*")) super.replaceSelection(text);
		correctText();
	}
	
	private void correctText() {
		int caretPosition = getCaretPosition() - getText().length();
		setText(getText().replaceAll("^0+(?!$)", ""));
		if (getText().length() == 0) setText("0");
		caretPosition += getText().length();
		positionCaret(caretPosition);
	}
	
	public int getValue() {
		return Integer.parseInt(getText());
	}
}
