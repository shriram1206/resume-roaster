package com.resumeroaster.exception;

/**
 * Exception thrown when resume file parsing fails (corrupted file or no readable text).
 */
public class ParsingException extends RuntimeException {

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
