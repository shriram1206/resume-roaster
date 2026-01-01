package com.resumeroaster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO containing the complete resume analysis results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResponse {

    /**
     * Database ID of the analysis record.
     */
    private Long analysisId;

    /**
     * Overall resume score (1-10 scale).
     */
    private Integer overallScore;

    /**
     * Tier placement category (e.g., "SERVICE_COMPANY", "PRODUCT_COMPANY", "STARTUP").
     */
    private String tierPlacement;

    /**
     * Summary verdict from the AI reviewer.
     */
    private String verdict;

    /**
     * List of top issues identified in the resume.
     */
    private List<String> topIssues;

    /**
     * List of actionable items to improve the resume.
     */
    private List<String> actionItems;
}
