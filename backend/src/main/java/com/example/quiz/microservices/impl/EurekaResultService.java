package com.example.quiz.microservices.impl;

import com.example.quiz.domain.User;
import com.example.quiz.microservices.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "microservices.mode", havingValue = "eureka")
public class EurekaResultService implements ResultService {

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate restTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getStudentResults(User user) {
        return restTemplate.getForObject("http://result-service/api/results/student/" + user.getId(), Map.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getQuizStatistics(Long quizId) {
        return restTemplate.getForObject("http://result-service/api/results/quiz/" + quizId, Map.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getLeaderboard() {
        Map<String, Object>[] results = restTemplate.getForObject("http://result-service/api/results/leaderboard",
                Map[].class);
        return results != null ? List.of(results) : List.of();
    }
}