package com.example.quiz.repo;

import com.example.quiz.domain.Question;
import com.example.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuiz(Quiz quiz);
    void deleteByQuiz(Quiz quiz);
}

