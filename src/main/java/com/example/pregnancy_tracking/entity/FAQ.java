package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "FAQs")
@Data
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long id;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "answer", nullable = false)
    private String answer;
}