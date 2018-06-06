package ca.bcit.utils.collections;

class HashArrayException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9190079811225051665L;

	public HashArrayException(String message, Object... args) {
		super(String.format(message, args));
	}
}
