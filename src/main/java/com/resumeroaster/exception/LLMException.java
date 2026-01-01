package com.resumeroaster.exception;

/**
 * Exception thrown when LLM (Ollama) call fails or returns invalid response.
 */
public class LLMException extends RuntimeException {

    public LLMException(String message) {
        super(message);
    }

    public LLMException(String message, Throwable cause) {
        super(message, cause);
    }
}
