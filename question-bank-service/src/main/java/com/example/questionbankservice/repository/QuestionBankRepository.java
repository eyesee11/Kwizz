package com.example.questionbankservice.repository;

import com.example.questionbankservice.domain.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findBySubject(String subject);

    List<QuestionBank> findByDifficulty(QuestionBank.Difficulty difficulty);
}