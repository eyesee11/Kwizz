package com.example.quiz.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    @JsonIgnore
    private Quiz quiz;

    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "text", column = @Column(name = "option_text")),
        @AttributeOverride(name = "correct", column = @Column(name = "is_correct"))
    })
    private List<QuestionOption> options;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    public static class QuestionOption {
        @Column(name = "option_text")
        private String text;
        
        @Column(name = "is_correct")
        private boolean correct;

        public QuestionOption(String text, boolean correct) {
            this.text = text;
            this.correct = correct;
        }
    }
}
