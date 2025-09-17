package com.example.quiz.web;

import com.example.quiz.domain.Quiz;
import com.example.quiz.domain.Question;
import com.example.quiz.service.QuizService;
import com.example.quiz.service.AIQuizGeneratorService;
import com.example.quiz.microservices.QuestionBankService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class QuizController {
    private final QuizService quizService;
    private final AIQuizGeneratorService aiQuizGeneratorService;
    private final QuestionBankService questionBankService;

    public QuizController(QuizService quizService, 
                         AIQuizGeneratorService aiQuizGeneratorService,
                         @Qualifier("questionBankMicroservice") QuestionBankService questionBankService) {
        this.quizService = quizService;
        this.aiQuizGeneratorService = aiQuizGeneratorService;
        this.questionBankService = questionBankService;
    }

    record CreateQuizRequest(@NotBlank String title, Quiz.Difficulty difficulty) {}
    record AIGenerateRequest(Quiz.Difficulty difficulty, String prompt) {}

    @GetMapping
    public List<Quiz> all() { return quizService.all(); }

    @PostMapping
    public Quiz create(@RequestBody CreateQuizRequest req, Authentication authentication) {
        // role enforcement can be added later or via Security
        return quizService.create(req.title(), req.difficulty());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available")
    public List<Quiz> available() { return quizService.all(); }
    
    @GetMapping("/{id}/questions")
    public List<Question> getQuestions(@PathVariable Long id) {
        return quizService.getQuestions(id);
    }
    
    @PutMapping("/{id}/questions")
    public ResponseEntity<?> saveQuestions(@PathVariable Long id, @RequestBody List<Question> questions) {
        quizService.saveQuestions(id, questions);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/ai-generate")
    public ResponseEntity<Map<String, Object>> generateWithAI(@PathVariable Long id, @RequestBody AIGenerateRequest req) {
        try {
            // Generate questions using AI service and save to question bank
            var questionBank = aiQuizGeneratorService.generateQuestionBank(
                req.prompt(), req.difficulty().toString(), 5);
            
            // Save to question bank microservice
            var savedBank = questionBankService.saveQuestionBankWithQuestions(questionBank);
            
            // Convert bank questions to quiz questions and save to quiz
            List<Question> quizQuestions = savedBank.getQuestions().stream()
                .map(bankQ -> {
                    Question q = new Question();
                    q.setText(bankQ.getText());
                    q.setOptions(bankQ.getOptions().stream()
                        .map(opt -> {
                            var qOpt = new Question.QuestionOption();
                            qOpt.setText(opt.getText());
                            qOpt.setCorrect(opt.isCorrect());
                            return qOpt;
                        }).toList());
                    return q;
                }).toList();
            
            quizService.saveQuestions(id, quizQuestions);
            
            return ResponseEntity.ok(Map.of(
                "questions", quizQuestions,
                "questionBankId", savedBank.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/questions/batch")
    public ResponseEntity<?> addQuestionsBatch(@PathVariable Long id, @RequestBody List<Question> questions) {
        quizService.saveQuestions(id, questions);
        return ResponseEntity.ok().build();
    }
}


