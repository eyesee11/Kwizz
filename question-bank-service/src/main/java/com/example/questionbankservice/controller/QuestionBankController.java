package com.example.questionbankservice.controller;

import com.example.questionbankservice.domain.QuestionBank;
import com.example.questionbankservice.repository.QuestionBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/question-banks")
public class QuestionBankController {

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @GetMapping
    public List<QuestionBank> getAllQuestionBanks() {
        return questionBankRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<QuestionBank> getQuestionBankById(@PathVariable Long id) {
        return questionBankRepository.findById(id);
    }

    @GetMapping("/subject/{subject}")
    public List<QuestionBank> getQuestionBanksBySubject(@PathVariable String subject) {
        return questionBankRepository.findBySubject(subject);
    }

    @GetMapping("/difficulty/{difficulty}")
    public List<QuestionBank> getQuestionBanksByDifficulty(@PathVariable QuestionBank.Difficulty difficulty) {
        return questionBankRepository.findByDifficulty(difficulty);
    }

    @PostMapping
    public QuestionBank createQuestionBank(@RequestBody QuestionBank questionBank) {
        return questionBankRepository.save(questionBank);
    }

    @PutMapping("/{id}")
    public QuestionBank updateQuestionBank(@PathVariable Long id, @RequestBody QuestionBank questionBank) {
        questionBank.setId(id);
        return questionBankRepository.save(questionBank);
    }

    @DeleteMapping("/{id}")
    public void deleteQuestionBank(@PathVariable Long id) {
        questionBankRepository.deleteById(id);
    }
}