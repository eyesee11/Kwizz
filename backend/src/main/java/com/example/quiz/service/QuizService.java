package com.example.quiz.service;

import com.example.quiz.domain.Quiz;
import com.example.quiz.domain.Question;
import com.example.quiz.repo.QuizRepository;
import com.example.quiz.repo.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    public Quiz create(String title, Quiz.Difficulty difficulty) {
        Quiz q = new Quiz();
        q.setTitle(title);
        q.setDifficulty(difficulty);
        return quizRepository.save(q);
    }

    public List<Quiz> all() { return quizRepository.findAll(); }
    public void delete(Long id) { quizRepository.deleteById(id); }
    
    public Optional<Quiz> findById(Long id) { return quizRepository.findById(id); }
    
    public List<Question> getQuestions(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        return questionRepository.findByQuiz(quiz);
    }
    
    public void saveQuestions(Long quizId, List<Question> questions) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        // Clear existing questions
        questionRepository.deleteByQuiz(quiz);
        // Save new questions
        for (Question question : questions) {
            question.setQuiz(quiz);
            questionRepository.save(question);
        }
    }
}


