package org.example.exceptions;

public class ExternalStorageException extends RuntimeException {
    public ExternalStorageException(String message) {
        super(message);
    }
    public ExternalStorageException(String message, Exception ex) {
        super(message);
    }
}
