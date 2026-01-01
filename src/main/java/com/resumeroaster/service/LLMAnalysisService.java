package com.resumeroaster.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeroaster.exception.LLMException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Service for interacting with Ollama LLM to analyze resume text.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LLMAnalysisService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ollama.model:llama3.1:8b}")
    private String model;

    @Value("${ollama.timeout:90}")
    private int timeoutSeconds;

    private static final String SYSTEM_PROMPT = """
        You are a brutal, honest resume reviewer for software engineering candidates targeting Tier 2 tech companies 
        (Zoho, Freshworks, PayTM, Flipkart, PhonePe). Be specific and actionable in your feedback.
        
        Analyze the resume and respond with ONLY valid JSON in this exact format:
        {
            "overallScore": <1-10>,
            "tierPlacement": "<SERVICE_COMPANY|PRODUCT_COMPANY|STARTUP|TOP_TIER>",
            "verdict": "<2-3 sentence summary>",
            "topIssues": ["issue1", "issue2", "issue3"],
            "actionItems": ["action1", "action2", "action3"]
        }
        """;

    /**
     * Analyze resume text using Ollama LLM.
     * @param resumeText the extracted text from the resume
     * @return JSON string with analysis results
     * @throws LLMException if LLM call fails or returns invalid response
     */
    public String analyzeWithOllama(String resumeText) {
        String prompt = SYSTEM_PROMPT + "\n\nResume:\n" + resumeText;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false,
                "format", "json",
                "options", Map.of(
                        "temperature", 0.3,
                        "num_predict", 2048
                )
        );

        try {
            log.info("Sending resume to Ollama for analysis...");

            String response = webClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .onErrorResume(e -> Mono.error(new LLMException("Ollama request failed: " + e.getMessage(), e)))
                    .block();

            if (response == null) {
                throw new LLMException("Empty response from Ollama");
            }

            // Extract the "response" field from Ollama's JSON response
            JsonNode rootNode = objectMapper.readTree(response);
            String analysisJson = rootNode.path("response").asText();

            if (analysisJson == null || analysisJson.isEmpty()) {
                throw new LLMException("No analysis content in Ollama response");
            }

            // Validate that the response contains overallScore
            validateAnalysisJson(analysisJson);

            log.info("Ollama analysis completed successfully");
            return analysisJson;

        } catch (LLMException e) {
            throw e;
        } catch (Exception e) {
            throw new LLMException("Failed to analyze resume with Ollama: " + e.getMessage(), e);
        }
    }

    /**
     * Validate that the analysis JSON contains required fields.
     */
    private void validateAnalysisJson(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            
            if (!node.has("overallScore")) {
                throw new LLMException("Analysis JSON missing required field: overallScore");
            }

            int score = node.get("overallScore").asInt();
            if (score < 1 || score > 10) {
                throw new LLMException("Invalid overallScore: must be between 1 and 10");
            }

        } catch (LLMException e) {
            throw e;
        } catch (Exception e) {
            throw new LLMException("Invalid analysis JSON format: " + e.getMessage(), e);
        }
    }
}
