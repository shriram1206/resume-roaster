package com.resumeroaster.controller;

import com.resumeroaster.dto.AnalysisRequest;
import com.resumeroaster.dto.AnalysisResponse;
import com.resumeroaster.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for triggering and retrieving resume analysis results.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    /**
     * Trigger resume analysis for a previously uploaded file.
     * @param request contains the uploadId from the upload step
     * @return analysis response with score, verdict, issues, and action items
     */
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyzeResume(@Valid @RequestBody AnalysisRequest request) {
        AnalysisResponse response = analysisService.analyzeResume(request.getUploadId());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a previously completed analysis by ID.
     * @param id the analysis ID
     * @return analysis response
     */
    @GetMapping("/analysis/{id}")
    public ResponseEntity<AnalysisResponse> getAnalysis(@PathVariable Long id) {
        AnalysisResponse response = analysisService.getAnalysisById(id);
        return ResponseEntity.ok(response);
    }
}
