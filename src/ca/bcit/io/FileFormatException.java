package ca.bcit.io;

class FileFormatException extends RuntimeException {

    private static final long serialVersionUID = -8925844132586929728L;

    public FileFormatException(String message) {
        super(message);
    }
}
