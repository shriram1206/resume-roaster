package com.resumeroaster.controller;

import com.resumeroaster.dto.UploadResponse;
import com.resumeroaster.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling resume file uploads.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UploadController {

    private final StorageService storageService;

    /**
     * Upload a resume file (PDF or Docx, max 5MB).
     * @param file the uploaded file
     * @return upload response with uploadId, filename, size, and status
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        UploadResponse response = storageService.storeFile(file);
        return ResponseEntity.ok(response);
    }
}
