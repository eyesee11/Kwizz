package com.example.quiz.web;

import com.example.quiz.domain.QuestionBank;
import com.example.quiz.domain.BankQuestion;
import com.example.quiz.microservices.QuestionBankService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question-bank")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    public QuestionBankController(@Qualifier("questionBankMicroservice") QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @PostMapping
    public ResponseEntity<QuestionBank> createQuestionBank(@RequestBody CreateBankRequest request) {
        try {
            QuestionBank.Difficulty difficulty = QuestionBank.Difficulty.valueOf(request.difficulty().toUpperCase());
            QuestionBank bank = questionBankService.createQuestionBank(request.subject(), difficulty);
            return ResponseEntity.ok(bank);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<QuestionBank>> getAllQuestionBanks() {
        return ResponseEntity.ok(questionBankService.getAllQuestionBanks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionBank> getQuestionBank(@PathVariable Long id) {
        return questionBankService.getQuestionBankById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<BankQuestion>> getQuestionsByBank(@PathVariable Long id) {
        return ResponseEntity.ok(questionBankService.getQuestionsByBankId(id));
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<BankQuestion> addQuestionToBank(@PathVariable Long id, @RequestBody AddQuestionRequest request) {
        try {
            List<BankQuestion.QuestionOption> options = request.options().stream()
                    .map(opt -> new BankQuestion.QuestionOption(opt.text(), opt.correct()))
                    .toList();
            
            BankQuestion question = questionBankService.addQuestionToBank(id, request.text(), options);
            return ResponseEntity.ok(question);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionBank(@PathVariable Long id) {
        try {
            questionBankService.deleteQuestionBank(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    record CreateBankRequest(String subject, String difficulty) {}
    record AddQuestionRequest(String text, List<OptionRequest> options) {}
    record OptionRequest(String text, boolean correct) {}
}
