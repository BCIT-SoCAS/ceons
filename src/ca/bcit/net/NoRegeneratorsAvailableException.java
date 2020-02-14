package ca.bcit.net;

public class NoRegeneratorsAvailableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoRegeneratorsAvailableException(String message) {
		super(message);
	}
}
