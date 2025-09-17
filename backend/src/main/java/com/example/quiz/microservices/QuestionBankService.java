package com.example.quiz.microservices;

import com.example.quiz.domain.QuestionBank;
import com.example.quiz.domain.BankQuestion;

import java.util.List;
import java.util.Optional;

public interface QuestionBankService {
    QuestionBank createQuestionBank(String subject, QuestionBank.Difficulty difficulty);

    List<QuestionBank> getAllQuestionBanks();

    Optional<QuestionBank> getQuestionBankById(Long id);

    List<BankQuestion> getQuestionsByBankId(Long bankId);

    BankQuestion addQuestionToBank(Long bankId, String text, List<BankQuestion.QuestionOption> options);

    List<QuestionBank> getQuestionBanksBySubject(String subject);

    List<QuestionBank> getQuestionBanksByDifficulty(QuestionBank.Difficulty difficulty);

    QuestionBank saveQuestionBankWithQuestions(QuestionBank questionBank);

    void deleteQuestionBank(Long id);
}