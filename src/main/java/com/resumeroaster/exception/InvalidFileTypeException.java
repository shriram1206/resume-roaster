package com.resumeroaster.exception;

/**
 * Exception thrown when uploaded file type is not supported (only PDF/Docx allowed).
 */
public class InvalidFileTypeException extends RuntimeException {

    public InvalidFileTypeException(String message) {
        super(message);
    }

    public InvalidFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
