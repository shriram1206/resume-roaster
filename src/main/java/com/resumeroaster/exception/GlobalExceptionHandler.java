package com.resumeroaster.exception;

import com.resumeroaster.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Global exception handler that maps custom exceptions to standardized HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle file size exceeded exception.
     * @return 413 Payload Too Large
     */
    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ErrorResponse> handleFileTooLarge(FileTooLargeException ex) {
        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "ERR_FILE_TOO_LARGE", ex.getMessage());
    }

    /**
     * Handle invalid file type exception.
     * @return 400 Bad Request
     */
    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileType(InvalidFileTypeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "ERR_INVALID_FILE_TYPE", ex.getMessage());
    }

    /**
     * Handle file parsing exception.
     * @return 400 Bad Request
     */
    @ExceptionHandler(ParsingException.class)
    public ResponseEntity<ErrorResponse> handleParsingException(ParsingException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "ERR_PARSING_FAILED", ex.getMessage());
    }

    /**
     * Handle LLM/Ollama exception.
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(LLMException.class)
    public ResponseEntity<ErrorResponse> handleLLMException(LLMException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_LLM_FAILED", ex.getMessage());
    }

    /**
     * Handle validation exceptions (e.g., @NotBlank failures).
     * @return 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return buildResponse(HttpStatus.BAD_REQUEST, "ERR_VALIDATION_FAILED", message);
    }

    /**
     * Handle all other unhandled exceptions.
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_INTERNAL", "An unexpected error occurred");
    }

    /**
     * Build standardized error response.
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String errorCode, String message) {
        ErrorResponse response = ErrorResponse.builder()
                .error(message)
                .errorCode(errorCode)
                .timestamp(Instant.now().toString())
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
