package com.example.quiz.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BankQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_bank_id")
    private QuestionBank questionBank;

    @ElementCollection
    @CollectionTable(name = "bank_question_options", joinColumns = @JoinColumn(name = "question_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "text", column = @Column(name = "option_text")),
        @AttributeOverride(name = "correct", column = @Column(name = "is_correct"))
    })
    private List<QuestionOption> options = new ArrayList<>();

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
