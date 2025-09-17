package com.example.quiz.web;

import com.example.quiz.domain.Attempt;
import com.example.quiz.domain.User;
import com.example.quiz.service.AttemptService;
import com.example.quiz.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attempt")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AttemptController {
    private final AttemptService attemptService;
    private final UserService userService;

    public AttemptController(AttemptService attemptService, UserService userService) {
        this.attemptService = attemptService;
        this.userService = userService;
    }

    private User getCurrentUser(Authentication authentication) { 
        String email = authentication.getName();
        return userService.findByEmail(email).orElseThrow();
    }

    @PostMapping
    public Attempt submitAttempt(@RequestBody AttemptRequest request, Authentication authentication) {
        User student = getCurrentUser(authentication);
        return attemptService.submitAttempt(request.quizId(), student, request.answers());
    }

    @GetMapping("/my")
    public List<Attempt> myAttempts(Authentication authentication) {
        return attemptService.attemptsFor(getCurrentUser(authentication));
    }

    public record AttemptRequest(Long quizId, List<Long> answers) {}
}


