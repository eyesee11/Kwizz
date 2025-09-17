package com.example.quiz.repo;

import com.example.quiz.domain.BankQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankQuestionRepository extends JpaRepository<BankQuestion, Long> {
    List<BankQuestion> findByQuestionBankId(Long questionBankId);
}
