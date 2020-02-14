package ca.bcit.net.spectrum;

public class NoSpectrumAvailableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoSpectrumAvailableException(String message) {
		super(message);
	}
}
