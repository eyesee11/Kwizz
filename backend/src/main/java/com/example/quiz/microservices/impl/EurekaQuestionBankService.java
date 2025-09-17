package com.example.quiz.microservices.impl;

import com.example.quiz.domain.QuestionBank;
import com.example.quiz.domain.BankQuestion;
import com.example.quiz.microservices.QuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("questionBankMicroservice")
@ConditionalOnProperty(name = "microservices.mode", havingValue = "eureka")
public class EurekaQuestionBankService implements QuestionBankService {

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public QuestionBank createQuestionBank(String subject, QuestionBank.Difficulty difficulty) {
        Map<String, Object> request = Map.of("subject", subject, "difficulty", difficulty.name());
        return restTemplate.postForObject("http://question-bank-service/api/question-banks", request,
                QuestionBank.class);
    }

    @Override
    public List<QuestionBank> getAllQuestionBanks() {
        QuestionBank[] result = restTemplate.getForObject("http://question-bank-service/api/question-banks",
                QuestionBank[].class);
        return result != null ? List.of(result) : List.of();
    }

    @Override
    public Optional<QuestionBank> getQuestionBankById(Long id) {
        try {
            QuestionBank result = restTemplate.getForObject("http://question-bank-service/api/question-banks/" + id,
                    QuestionBank.class);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<BankQuestion> getQuestionsByBankId(Long bankId) {
        BankQuestion[] result = restTemplate.getForObject(
                "http://question-bank-service/api/question-banks/" + bankId + "/questions", BankQuestion[].class);
        return result != null ? List.of(result) : List.of();
    }

    @Override
    public BankQuestion addQuestionToBank(Long bankId, String text, List<BankQuestion.QuestionOption> options) {
        Map<String, Object> request = Map.of("text", text, "options", options);
        return restTemplate.postForObject("http://question-bank-service/api/question-banks/" + bankId + "/questions",
                request, BankQuestion.class);
    }

    @Override
    public List<QuestionBank> getQuestionBanksBySubject(String subject) {
        QuestionBank[] result = restTemplate.getForObject(
                "http://question-bank-service/api/question-banks/subject/" + subject, QuestionBank[].class);
        return result != null ? List.of(result) : List.of();
    }

    @Override
    public List<QuestionBank> getQuestionBanksByDifficulty(QuestionBank.Difficulty difficulty) {
        QuestionBank[] result = restTemplate.getForObject(
                "http://question-bank-service/api/question-banks/difficulty/" + difficulty.name(),
                QuestionBank[].class);
        return result != null ? List.of(result) : List.of();
    }

    @Override
    public QuestionBank saveQuestionBankWithQuestions(QuestionBank questionBank) {
        if (questionBank.getId() == null) {
            return restTemplate.postForObject("http://question-bank-service/api/question-banks", questionBank,
                    QuestionBank.class);
        } else {
            restTemplate.put("http://question-bank-service/api/question-banks/" + questionBank.getId(), questionBank);
            return questionBank;
        }
    }

    @Override
    public void deleteQuestionBank(Long id) {
        restTemplate.delete("http://question-bank-service/api/question-banks/" + id);
    }
}