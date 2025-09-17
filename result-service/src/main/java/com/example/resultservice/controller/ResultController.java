package com.example.resultservice.controller;

import com.example.resultservice.domain.Attempt;
import com.example.resultservice.domain.User;
import com.example.resultservice.repository.AttemptRepository;
import com.example.resultservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/student/{studentId}")
    public Map<String, Object> getStudentResults(@PathVariable Long studentId) {
        Optional<User> userOpt = userRepository.findById(studentId);
        if (userOpt.isEmpty()) {
            return Map.of("error", "Student not found");
        }

        User user = userOpt.get();
        List<Attempt> attempts = attemptRepository.findByStudent(user);
        DoubleSummaryStatistics stats = attempts.stream().mapToDouble(Attempt::getScore).summaryStatistics();

        Map<String, Object> result = new HashMap<>();
        result.put("studentId", user.getId());
        result.put("attempts", attempts.size());
        result.put("avgScore", attempts.isEmpty() ? 0 : stats.getAverage());
        result.put("bestScore", attempts.isEmpty() ? 0 : stats.getMax());
        return result;
    }

    @GetMapping("/quiz/{quizId}")
    public Map<String, Object> getQuizStatistics(@PathVariable Long quizId) {
        List<Attempt> attempts = attemptRepository.findByQuizId(quizId);
        DoubleSummaryStatistics stats = attempts.stream().mapToDouble(Attempt::getScore).summaryStatistics();

        Map<String, Object> result = new HashMap<>();
        result.put("quizId", quizId);
        result.put("attempts", attempts.size());
        result.put("avgScore", attempts.isEmpty() ? 0 : stats.getAverage());
        result.put("bestScore", attempts.isEmpty() ? 0 : stats.getMax());
        return result;
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        List<User> users = userRepository.findAll();
        Map<Long, List<Attempt>> attemptsByUser = attemptRepository.findAll().stream()
                .collect(Collectors.groupingBy(a -> a.getStudent().getId()));

        return users.stream().map(u -> {
            List<Attempt> attempts = attemptsByUser.getOrDefault(u.getId(), List.of());
            DoubleSummaryStatistics stats = attempts.stream().mapToDouble(Attempt::getScore).summaryStatistics();
            Map<String, Object> entry = new HashMap<>();
            entry.put("studentId", u.getId());
            entry.put("name", u.getName());
            entry.put("attempts", attempts.size());
            entry.put("avgScore", attempts.isEmpty() ? 0 : stats.getAverage());
            return entry;
        }).sorted((a, b) -> Double.compare((double) b.get("avgScore"), (double) a.get("avgScore")))
                .limit(10)
                .toList();
    }
}