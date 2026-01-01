package com.resumeroaster.exception;

/**
 * Exception thrown when uploaded file exceeds the maximum allowed size (5MB).
 */
public class FileTooLargeException extends RuntimeException {

    public FileTooLargeException(String message) {
        super(message);
    }

    public FileTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }
}
