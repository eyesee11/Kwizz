package com.example.quiz.service;

import com.example.quiz.domain.BankQuestion;
import com.example.quiz.domain.QuestionBank;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AIQuizGeneratorService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AIQuizGeneratorService(@Qualifier("aiRestTemplate") RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    /**
     * Generates a quiz using the Gemini API
     * 
     * @param prompt     The prompt for question generation
     * @param difficulty The difficulty level
     * @return A QuestionBank object with generated questions
     */
    public QuestionBank generateQuiz(String prompt, String difficulty) {
        try {
            // Create the prompt for Gemini API
            String fullPrompt = createPrompt(prompt, difficulty, 5);

            // Call Gemini API
            String response = callGeminiAPI(fullPrompt);

            // Parse the response
            return parseResponse(response, prompt, difficulty);
        } catch (Exception e) {
            log.error("Error generating quiz", e);
            throw new RuntimeException("Failed to generate quiz using AI", e);
        }
    }

    /**
     * Generates a question bank using the Gemini API
     * 
     * @param subject           The subject for the questions
     * @param difficulty        The difficulty level
     * @param numberOfQuestions The number of questions to generate
     * @return A QuestionBank object with generated questions
     */
    public QuestionBank generateQuestionBank(String subject, String difficulty, int numberOfQuestions) {
        try {
            // Create the prompt for Gemini API
            String prompt = createPrompt(subject, difficulty, numberOfQuestions);

            // Call Gemini API
            String response = callGeminiAPI(prompt);

            // Parse the response
            return parseResponse(response, subject, difficulty);
        } catch (Exception e) {
            log.error("Error generating question bank", e);
            throw new RuntimeException("Failed to generate questions using AI", e);
        }
    }

    private String createPrompt(String subject, String difficulty, int numberOfQuestions) {
        return String.format(
                "Generate %d multiple-choice questions about %s with %s difficulty. " +
                        "Return ONLY a valid JSON array without any markdown formatting or code blocks. " +
                        "Each question should have exactly 4 options with only one correct answer. " +
                        "Use this exact format: " +
                        "[{\"question\": \"Question text here?\", \"options\": [" +
                        "{\"text\": \"Option A\", \"correct\": true}, " +
                        "{\"text\": \"Option B\", \"correct\": false}, " +
                        "{\"text\": \"Option C\", \"correct\": false}, " +
                        "{\"text\": \"Option D\", \"correct\": false}]}]. " +
                        "Do not include any explanations, just the JSON array.",
                numberOfQuestions, subject, difficulty);
    }

    private String callGeminiAPI(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException(
                    "Gemini API key is not configured. Please set gemini.api.key in application.properties");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> contents = new HashMap<>();
        Map<String, Object> parts = new HashMap<>();

        parts.put("text", prompt);
        contents.put("parts", List.of(parts));
        requestBody.put("contents", List.of(contents));
        requestBody.put("generationConfig", Map.of(
                "temperature", 0.7,
                "topK", 40,
                "topP", 0.95,
                "maxOutputTokens", 1024));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String url = apiUrl + "?key=" + apiKey;
        String response = restTemplate.postForObject(url, entity, String.class);

        try {
            JsonNode responseJson = objectMapper.readTree(response);
            return responseJson.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();
        } catch (Exception e) {
            log.error("Error parsing Gemini API response", e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private QuestionBank parseResponse(String response, String subject, String difficulty) {
        try {
            // Create a new question bank
            QuestionBank questionBank = new QuestionBank();
            questionBank.setSubject(subject);
            questionBank.setDifficulty(QuestionBank.Difficulty.valueOf(difficulty.toUpperCase()));

            // Clean the response - remove markdown code blocks if present
            String cleanedResponse = cleanJsonResponse(response);
            log.debug("Cleaned response: {}", cleanedResponse);

            // Parse the JSON response
            JsonNode questionsArray = objectMapper.readTree(cleanedResponse);
            List<BankQuestion> questions = new ArrayList<>();

            for (JsonNode questionNode : questionsArray) {
                BankQuestion question = new BankQuestion();
                question.setText(questionNode.path("question").asText());
                question.setQuestionBank(questionBank);

                List<BankQuestion.QuestionOption> options = new ArrayList<>();
                for (JsonNode optionNode : questionNode.path("options")) {
                    BankQuestion.QuestionOption option = new BankQuestion.QuestionOption(
                            optionNode.path("text").asText(),
                            optionNode.path("correct").asBoolean());
                    options.add(option);
                }

                question.setOptions(options);
                questions.add(question);
            }

            questionBank.setQuestions(questions);
            return questionBank;
        } catch (Exception e) {
            log.error("Error parsing AI response to question bank", e);
            throw new RuntimeException("Failed to parse AI-generated questions", e);
        }
    }

    /**
     * Cleans the JSON response by removing markdown code blocks and other
     * formatting
     */
    private String cleanJsonResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return response;
        }

        String cleaned = response.trim();

        // Remove markdown code blocks (```json ... ``` or ``` ... ```)
        if (cleaned.startsWith("```")) {
            int firstNewline = cleaned.indexOf('\n');
            if (firstNewline != -1) {
                cleaned = cleaned.substring(firstNewline + 1);
            }
        }

        if (cleaned.endsWith("```")) {
            int lastBackticks = cleaned.lastIndexOf("```");
            if (lastBackticks != -1) {
                cleaned = cleaned.substring(0, lastBackticks);
            }
        }

        // Remove any leading/trailing whitespace
        cleaned = cleaned.trim();

        // If the response doesn't start with [ or {, try to find the JSON part
        if (!cleaned.startsWith("[") && !cleaned.startsWith("{")) {
            int jsonStart = Math.max(cleaned.indexOf('['), cleaned.indexOf('{'));
            if (jsonStart != -1) {
                cleaned = cleaned.substring(jsonStart);
            }
        }

        return cleaned;
    }
}