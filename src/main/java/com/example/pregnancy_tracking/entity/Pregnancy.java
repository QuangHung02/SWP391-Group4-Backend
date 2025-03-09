package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Pregnancies")
@Getter
@Setter
public class Pregnancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pregnancy_id")
    private Long pregnancyId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "gestational_weeks", nullable = false)
    private Integer gestationalWeeks;

    @Column(name = "gestational_days", nullable = false)
    private Integer gestationalDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PregnancyStatus status;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "total_fetuses", nullable = false)
    private Integer totalFetuses;

    @OneToMany(mappedBy = "pregnancy", cascade = CascadeType.ALL)
    private List<Fetus> fetuses;
}
