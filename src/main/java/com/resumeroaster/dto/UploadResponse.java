package com.resumeroaster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned after successful file upload.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {

    /**
     * Unique identifier for the uploaded file (UUID format).
     */
    private String uploadId;

    /**
     * Original filename of the uploaded file.
     */
    private String filename;

    /**
     * Size of the uploaded file in bytes.
     */
    private Long fileSize;

    /**
     * Status of the upload (e.g., "uploaded").
     */
    private String status;
}
