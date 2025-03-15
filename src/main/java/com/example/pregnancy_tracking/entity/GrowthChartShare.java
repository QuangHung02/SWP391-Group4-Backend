package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Table(name = "growth_chart_shares")
public class GrowthChartShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private CommunityPost post;

    @ManyToOne
    @JoinColumn(name = "fetus_id")
    private Fetus fetus;

    @ElementCollection
    @CollectionTable(name = "shared_chart_types", 
                    joinColumns = @JoinColumn(name = "share_id"))
    @Column(name = "chart_type")
    @Enumerated(EnumType.STRING)
    private Set<ChartType> sharedTypes;

    @Column(name = "chart_data", columnDefinition = "TEXT")
    private String chartData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}