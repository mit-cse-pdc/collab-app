package com.pdc.questionbankservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "answers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "question")
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "explanation")
    private String explanation;
}