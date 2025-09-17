package com.example.quiz.microservices;

import com.example.quiz.domain.User;

import java.util.List;
import java.util.Map;

public interface ResultService {
    Map<String, Object> getStudentResults(User user);

    Map<String, Object> getQuizStatistics(Long quizId);

    List<Map<String, Object>> getLeaderboard();
}