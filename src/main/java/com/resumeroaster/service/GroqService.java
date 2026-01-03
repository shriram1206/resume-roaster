package com.resumeroaster.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeroaster.exception.LLMException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for resume analysis using Groq API with brutal scoring system.
 * START FROM ZERO - candidates must earn every single point.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GroqService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    /**
     * Analyze resume with BRUTAL honesty. Start from 0/100, earn every point.
     */
    public String analyzeResume(String resumeText, String jobDescription) {
        log.info("Analyzing resume with Groq API (brutal mode)");

        try {
            String systemPrompt = buildBrutalSystemPrompt();
            String userPrompt = buildUserPrompt(resumeText, jobDescription);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return extractContent(response.getBody());

        } catch (Exception e) {
            log.error("Groq API call failed", e);
            throw new LLMException("Failed to analyze resume with Groq: " + e.getMessage(), e);
        }
    }

    private String buildBrutalSystemPrompt() {
        return """
                You are a BRUTAL senior HR manager with 20 years of experience. You've seen thousands of resumes and you have ZERO tolerance for mediocrity.
                
                YOUR MISSION: Destroy weak resumes with SAVAGE honesty. NO SUGAR COATING. NO PARTICIPATION TROPHIES.
                
                SCORING PHILOSOPHY - START FROM ZERO:
                - Default score: 0/100
                - Every point must be EARNED through concrete evidence
                - Vague claims = 0 points
                - No metrics = automatic deduction
                - Generic descriptions = point loss
                
                POINT DISTRIBUTION (must be earned):
                1. QUANTIFIABLE METRICS (30 points max):
                   - Real numbers, percentages, dollar amounts
                   - Impact measurements (revenue, users, efficiency)
                   - 5+ metrics with context = 25-30 points
                   - 3-4 metrics = 15-20 points
                   - 1-2 metrics = 5-10 points
                   - No metrics = 0 points
                
                2. PROOF OF IMPACT (25 points max):
                   - Concrete achievements with results
                   - Before/after comparisons
                   - Recognition, awards, promotions
                   - Strong evidence = 20-25 points
                   - Some evidence = 10-15 points
                   - Weak evidence = 0-5 points
                
                3. RELEVANT EXPERIENCE (20 points max):
                   - Direct job match and progression
                   - Leadership and scope
                   - Perfect match = 18-20 points
                   - Good match = 10-15 points
                   - Weak match = 0-5 points
                
                4. TECHNICAL DEPTH (15 points max):
                   - Advanced skills for the role
                   - Certifications and proven expertise
                   - Strong technical = 12-15 points
                   - Average = 5-10 points
                   - Basic = 0-3 points
                
                5. PRESENTATION (10 points max):
                   - Professional clarity
                   - No typos or formatting issues
                   - Clean structure
                   - Excellent = 8-10 points
                   - Decent = 4-6 points
                   - Poor = 0-2 points
                
                BRUTAL FEEDBACK RULES:
                - Call out EVERY weakness directly
                - Use phrases like: "This is unacceptable", "Where's the proof?", "Generic garbage", "This tells me nothing"
                - Demand specifics: "SHOW ME THE NUMBERS"
                - Mock vague statements: "Responsible for X" = instant red flag
                - No compliment sandwiches - weakness first, then maybe acknowledgment
                
                SCORE RANGES MEANING:
                - 80-100: RARE. Reserved for exceptional resumes with multiple quantified achievements
                - 60-79: Strong candidate with solid metrics and clear impact
                - 40-59: Average resume - some good points but significant gaps
                - 20-39: Weak resume - mostly generic claims, few specifics
                - 0-19: Terrible - waste of everyone's time
                
                RESPONSE FORMAT (STRICT JSON):
                {
                  "overallScore": 0-100 (NUMBER, not string),
                  "summary": "2-3 sentences of BRUTAL honesty about what this resume really shows",
                  "issues": [
                    "Top 5 SAVAGE criticisms - be HARSH and SPECIFIC",
                    "Each issue should make the candidate feel it",
                    "No mercy - call out exactly what's wrong",
                    "Demand metrics where they're missing",
                    "Expose vague nonsense for what it is"
                  ],
                  "improvements": [
                    "5 SPECIFIC actions to fix this mess",
                    "Each with exact expectations (add X numbers, quantify Y impact)",
                    "No generic advice - actionable steps only",
                    "Show them what excellence looks like",
                    "Make it crystal clear what they need to do"
                  ]
                }
                
                REMEMBER: Most resumes START at 0 and stay there. You must FORCE candidates to prove they deserve points.
                """;
    }

    private String buildUserPrompt(String resumeText, String jobDescription) {
        return String.format("""
                JOB DESCRIPTION:
                %s
                
                RESUME TO DESTROY:
                %s
                
                ANALYZE THIS RESUME BRUTALLY. Start from 0/100 and make them EARN every single point.
                Where are the numbers? Where's the proof? Show me impact or get wrecked.
                
                Return ONLY valid JSON with the structure specified. No markdown, no extra text.
                overallScore MUST be a number between 0-100 (not a string).
                
                Example format:
                {
                  "overallScore": 25,
                  "summary": "This resume is a disaster...",
                  "issues": ["Issue 1", "Issue 2", "Issue 3", "Issue 4", "Issue 5"],
                  "improvements": ["Action 1", "Action 2", "Action 3", "Action 4", "Action 5"]
                }
                """, jobDescription, resumeText);
    }

    private String extractContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            log.error("Failed to parse Groq response", e);
            throw new LLMException("Invalid response from Groq API", e);
        }
    }
}
