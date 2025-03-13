package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

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
    @JsonIgnoreProperties("pregnancies")
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
    
    @OneToMany(mappedBy = "pregnancy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("pregnancy")
    private List<Fetus> fetuses = new ArrayList<>();

    public List<Fetus> getFetuses() {
        return fetuses != null ? fetuses : new ArrayList<>();
    }

    public void setFetuses(List<Fetus> fetuses) {
        this.fetuses = fetuses != null ? fetuses : new ArrayList<>();
    }
}
