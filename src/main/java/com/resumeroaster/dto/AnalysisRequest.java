package com.resumeroaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for triggering resume analysis.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisRequest {

    /**
     * Upload ID from the previous upload step (UUID format).
     */
    @NotBlank(message = "Upload ID is required")
    private String uploadId;
}
