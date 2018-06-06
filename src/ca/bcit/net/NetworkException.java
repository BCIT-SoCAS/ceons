package ca.bcit.net;

public class NetworkException extends RuntimeException {

	private static final long serialVersionUID = -1385146508031639810L;

	public NetworkException(String message) {
		super(message);
	}
}
