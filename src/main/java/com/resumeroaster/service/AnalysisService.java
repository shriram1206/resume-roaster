package com.resumeroaster.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeroaster.dto.AnalysisResponse;
import com.resumeroaster.entity.Analysis;
import com.resumeroaster.exception.LLMException;
import com.resumeroaster.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Service orchestrating the complete resume analysis workflow.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisService {

    private final StorageService storageService;
    private final ResumeParserService resumeParserService;
    private final LLMAnalysisService llmAnalysisService;
    private final AnalysisRepository analysisRepository;
    private final ObjectMapper objectMapper;

    /**
     * Perform full analysis on an uploaded resume.
     * @param uploadId the upload identifier
     * @return analysis response DTO
     */
    @Transactional
    public AnalysisResponse analyzeResume(String uploadId) {
        log.info("Starting analysis for uploadId: {}", uploadId);

        // 1. Get file path
        Path filePath = storageService.getFilePath(uploadId);
        String filename = storageService.getOriginalFilename(uploadId);
        long fileSize = storageService.getFileSize(uploadId);

        // 2. Parse resume text
        String parsedText = resumeParserService.parseResume(filePath);

        // 3. Analyze with LLM
        String analysisJson = llmAnalysisService.analyzeWithOllama(parsedText);

        // 4. Extract fields from JSON
        JsonNode jsonNode = parseJson(analysisJson);
        Integer overallScore = jsonNode.path("overallScore").asInt();
        String tierPlacement = jsonNode.path("tierPlacement").asText(null);

        // 5. Save to database
        Analysis analysis = Analysis.builder()
                .filename(filename)
                .fileSize(fileSize)
                .uploadedAt(LocalDateTime.now())
                .parsedText(parsedText)
                .analysisJson(analysisJson)
                .overallScore(overallScore)
                .tierPlacement(tierPlacement)
                .build();

        analysis = analysisRepository.save(analysis);
        log.info("Analysis saved with id: {}", analysis.getId());

        // 6. Build and return response
        return buildResponse(analysis, jsonNode);
    }

    /**
     * Retrieve an existing analysis by ID.
     * @param id the analysis ID
     * @return analysis response DTO
     */
    public AnalysisResponse getAnalysisById(Long id) {
        Analysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found: " + id));

        JsonNode jsonNode = parseJson(analysis.getAnalysisJson());
        return buildResponse(analysis, jsonNode);
    }

    private JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new LLMException("Failed to parse analysis JSON", e);
        }
    }

    private AnalysisResponse buildResponse(Analysis analysis, JsonNode jsonNode) {
        List<String> topIssues = extractStringList(jsonNode, "topIssues");
        List<String> actionItems = extractStringList(jsonNode, "actionItems");

        return AnalysisResponse.builder()
                .analysisId(analysis.getId())
                .overallScore(analysis.getOverallScore())
                .tierPlacement(analysis.getTierPlacement())
                .verdict(jsonNode.path("verdict").asText(null))
                .topIssues(topIssues)
                .actionItems(actionItems)
                .build();
    }

    private List<String> extractStringList(JsonNode node, String fieldName) {
        try {
            JsonNode arrayNode = node.path(fieldName);
            if (arrayNode.isArray()) {
                return objectMapper.convertValue(arrayNode, new TypeReference<List<String>>() {});
            }
        } catch (Exception e) {
            log.warn("Failed to extract {}: {}", fieldName, e.getMessage());
        }
        return Collections.emptyList();
    }
}
