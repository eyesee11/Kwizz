package com.example.quiz.web;

import com.example.quiz.domain.User;
import com.example.quiz.microservices.ResultService;
import com.example.quiz.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:5173")
public class ResultController {
    private final ResultService resultService;
    private final UserService userService;

    public ResultController(ResultService resultService, UserService userService) {
        this.resultService = resultService;
        this.userService = userService;
    }

    @GetMapping("/my")
    public Map<String, Object> getMyResults(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email).orElseThrow();
        return resultService.getStudentResults(user);
    }

    @GetMapping("/quiz/{quizId}")
    public Map<String, Object> getQuizStatistics(@PathVariable Long quizId) {
        return resultService.getQuizStatistics(quizId);
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        return resultService.getLeaderboard();
    }
}
