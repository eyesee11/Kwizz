package com.example.quiz.web;

import com.example.quiz.domain.QuestionBank;
import com.example.quiz.service.AIQuizGeneratorService;
import com.example.quiz.microservices.QuestionBankService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AIController {
    
    private final AIQuizGeneratorService aiQuizGeneratorService;
    private final QuestionBankService questionBankService;
    
    public AIController(AIQuizGeneratorService aiQuizGeneratorService, 
                       @Qualifier("questionBankMicroservice") QuestionBankService questionBankService) {
        this.aiQuizGeneratorService = aiQuizGeneratorService;
        this.questionBankService = questionBankService;
    }
    
    record GenerateRequest(String subject, String difficulty, int numberOfQuestions) {}
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateQuiz(@RequestBody GenerateRequest request) {
        try {
            // Generate and save the question bank using microservice
            QuestionBank questionBank = aiQuizGeneratorService.generateQuestionBank(
                request.subject(), request.difficulty(), request.numberOfQuestions());
            
            // Save using Question Bank microservice
            QuestionBank savedBank = questionBankService.saveQuestionBankWithQuestions(questionBank);
            
            // Convert to format expected by frontend
            List<Map<String, Object>> questions = new ArrayList<>();
            savedBank.getQuestions().forEach(q -> {
                List<Map<String, Object>> options = new ArrayList<>();
                q.getOptions().forEach(opt -> {
                    options.add(Map.of(
                        "text", opt.getText(),
                        "correct", opt.isCorrect()
                    ));
                });
                questions.add(Map.of(
                    "text", q.getText(),
                    "options", options
                ));
            });
            
            Map<String, Object> response = Map.of(
                "id", savedBank.getId(),
                "subject", savedBank.getSubject(),
                "difficulty", savedBank.getDifficulty().toString(),
                "questions", questions
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to generate quiz: " + e.getMessage()));
        }
    }
}
