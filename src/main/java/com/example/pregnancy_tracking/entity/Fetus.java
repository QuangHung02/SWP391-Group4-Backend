package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Fetuses")  // Đảm bảo khớp với tên bảng trong database
@Getter
@Setter
public class Fetus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fetusId;

    @ManyToOne
    @JoinColumn(name = "pregnancy_id", nullable = false)
    private Pregnancy pregnancy;

    private Integer fetusIndex; // 1 = Bé đầu tiên, 2 = Bé thứ hai, ...
}
