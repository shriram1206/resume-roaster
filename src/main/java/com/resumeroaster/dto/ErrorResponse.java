package com.resumeroaster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized error response DTO for API error handling.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Human-readable error message.
     */
    private String error;

    /**
     * Machine-readable error code (e.g., "ERR_FILE_TOO_LARGE").
     */
    private String errorCode;

    /**
     * ISO 8601 timestamp of when the error occurred.
     */
    private String timestamp;
}
