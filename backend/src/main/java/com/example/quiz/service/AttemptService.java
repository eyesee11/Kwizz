package com.example.quiz.service;

import com.example.quiz.domain.Attempt;
import com.example.quiz.domain.Question;
import com.example.quiz.domain.Quiz;
import com.example.quiz.domain.User;
import com.example.quiz.repo.AttemptRepository;
import com.example.quiz.repo.QuestionRepository;
import com.example.quiz.repo.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttemptService {
    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public AttemptService(AttemptRepository attemptRepository, QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.attemptRepository = attemptRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    public Attempt saveAttempt(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public List<Attempt> getAttemptsByStudent(User student) {
        return attemptRepository.findByStudent(student);
    }

    public List<Attempt> getAttemptsByQuiz(Quiz quiz) {
        return attemptRepository.findByQuiz(quiz);
    }

    public Optional<Attempt> getAttemptById(Long id) {
        return attemptRepository.findById(id);
    }

    public List<Attempt> getAllAttempts() {
        return attemptRepository.findAll();
    }

    public void deleteAttempt(Long id) {
        attemptRepository.deleteById(id);
    }

    public Attempt attempt(User student, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));
        
        Attempt attempt = new Attempt();
        attempt.setStudent(student);
        attempt.setQuiz(quiz);
        attempt.setScore(0); // Initial score
        
        return attemptRepository.save(attempt);
    }

    public List<Attempt> attemptsFor(User student) {
        return attemptRepository.findByStudent(student);
    }

    public Attempt submitAttempt(Long quizId, User student, List<Long> answers) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));
        
        // Calculate score based on answers
        int score = calculateScore(quizId, answers);
        
        Attempt attempt = new Attempt();
        attempt.setStudent(student);
        attempt.setQuiz(quiz);
        attempt.setScore(score);
        
        return attemptRepository.save(attempt);
    }

    private int calculateScore(Long quizId, List<Long> answers) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));
        
        List<Question> questions = questionRepository.findByQuiz(quiz);
        
        if (questions.isEmpty()) {
            return 0;
        }
        
        int correctAnswers = 0;
        
        // Compare each answer with the correct option
        for (int i = 0; i < Math.min(questions.size(), answers.size()); i++) {
            Question question = questions.get(i);
            Long studentAnswer = answers.get(i); // This is the option index
            
            // Check if the student's answer index corresponds to a correct option
            if (studentAnswer != null && studentAnswer >= 0 && studentAnswer < question.getOptions().size()) {
                Question.QuestionOption selectedOption = question.getOptions().get(studentAnswer.intValue());
                if (selectedOption.isCorrect()) {
                    correctAnswers++;
                }
            }
        }
        
        // Calculate percentage score
        return (int) Math.round((double) correctAnswers / questions.size() * 100);
    }
}
