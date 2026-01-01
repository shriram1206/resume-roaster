package com.resumeroaster.service;

import com.resumeroaster.dto.UploadResponse;
import com.resumeroaster.exception.FileTooLargeException;
import com.resumeroaster.exception.InvalidFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for validating and storing uploaded resume files.
 */
@Service
@Slf4j
public class StorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"  // Added for testing
    );

    @Value("${app.upload.dir:${java.io.tmpdir}/resume-roaster-uploads}")
    private String uploadDir;

    // In-memory map to track uploadId -> file path (for MVP)
    private final ConcurrentHashMap<String, Path> uploadedFiles = new ConcurrentHashMap<>();

    /**
     * Validate and store the uploaded file.
     * @param file the uploaded multipart file
     * @return upload response with uploadId
     */
    public UploadResponse storeFile(MultipartFile file) {
        validateFile(file);

        String uploadId = UUID.randomUUID().toString();
        Path filePath = saveFile(file, uploadId);

        uploadedFiles.put(uploadId, filePath);

        log.info("File uploaded: uploadId={}, filename={}, size={}", 
                uploadId, file.getOriginalFilename(), file.getSize());

        return UploadResponse.builder()
                .uploadId(uploadId)
                .filename(file.getOriginalFilename())
                .fileSize(file.getSize())
                .status("uploaded")
                .build();
    }

    /**
     * Get the file path for a given uploadId.
     * @param uploadId the upload identifier
     * @return the file path
     */
    public Path getFilePath(String uploadId) {
        Path path = uploadedFiles.get(uploadId);
        if (path == null) {
            throw new IllegalArgumentException("Upload not found: " + uploadId);
        }
        return path;
    }

    /**
     * Get the original filename for a given uploadId.
     * @param uploadId the upload identifier
     * @return the original filename
     */
    public String getOriginalFilename(String uploadId) {
        Path path = getFilePath(uploadId);
        return path.getFileName().toString().substring(37); // Remove UUID prefix
    }

    /**
     * Get the file size for a given uploadId.
     * @param uploadId the upload identifier
     * @return file size in bytes
     */
    public long getFileSize(String uploadId) {
        try {
            return Files.size(getFilePath(uploadId));
        } catch (IOException e) {
            throw new RuntimeException("Failed to get file size", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileTypeException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileTooLargeException(
                    String.format("File size %d bytes exceeds maximum allowed size of %d bytes", 
                            file.getSize(), MAX_FILE_SIZE));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException(
                    "Invalid file type: " + contentType + ". Allowed types: PDF, DOCX");
        }
    }

    private Path saveFile(MultipartFile file, String uploadId) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = uploadId + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath);

            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
