package com.example.quiz.repo;

import com.example.quiz.domain.Attempt;
import com.example.quiz.domain.Quiz;
import com.example.quiz.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByStudent(User user);
    List<Attempt> findByQuiz(Quiz quiz);
    List<Attempt> findByQuizId(Long quizId);
}


