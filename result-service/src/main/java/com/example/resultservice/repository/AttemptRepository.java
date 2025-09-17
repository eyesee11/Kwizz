package com.example.resultservice.repository;

import com.example.resultservice.domain.Attempt;
import com.example.resultservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByStudent(User student);

    List<Attempt> findByQuizId(Long quizId);
}