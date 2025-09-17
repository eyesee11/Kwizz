package com.example.quiz.web;

import com.example.quiz.domain.Attempt;
import com.example.quiz.domain.User;
import com.example.quiz.domain.Role;
import com.example.quiz.repo.AttemptRepository;
import com.example.quiz.repo.UserRepository;
import com.example.quiz.microservices.ResultService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AnalyticsController {
    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final ResultService resultService;

    public AnalyticsController(AttemptRepository attemptRepository, UserRepository userRepository, ResultService resultService) {
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.resultService = resultService;
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        var all = attemptRepository.findAll();
        DoubleSummaryStatistics stats = all.stream().mapToDouble(Attempt::getScore).summaryStatistics();
        return Map.of(
            "attemptCount", all.size(),
            "avgScore", stats.getAverage(),
            "maxScore", stats.getMax(),
            "minScore", stats.getMin()
        );
    }

    @GetMapping("/students")
    public List<Map<String, Object>> students() {
        List<User> students = userRepository.findAll().stream().filter(u -> u.getRole() == Role.STUDENT).toList();
        var attempts = attemptRepository.findAll();
        Map<Long, List<Attempt>> byStudent = attempts.stream().collect(Collectors.groupingBy(a -> a.getStudent().getId()));
        return students.stream().map(s -> {
            var list = byStudent.getOrDefault(s.getId(), List.of());
            DoubleSummaryStatistics st = list.stream().mapToDouble(Attempt::getScore).summaryStatistics();
            Map<String, Object> result = new HashMap<>();
            result.put("studentId", s.getId());
            result.put("name", s.getName());
            result.put("attempts", list.size());
            result.put("avgScore", list.isEmpty() ? 0 : st.getAverage());
            return result;
        }).toList();
    }

    @GetMapping("/attempts")
    public List<Map<String, Object>> attempts() {
        return attemptRepository.findAll().stream().map(a -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", a.getId());
            result.put("studentId", a.getStudent().getId());
            result.put("studentName", a.getStudent().getName());
            result.put("quizId", a.getQuiz().getId());
            result.put("quizTitle", a.getQuiz().getTitle());
            result.put("score", a.getScore());
            result.put("createdAt", a.getCreatedAt());
            return result;
        }).toList();
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        return resultService.getLeaderboard();
    }
}


