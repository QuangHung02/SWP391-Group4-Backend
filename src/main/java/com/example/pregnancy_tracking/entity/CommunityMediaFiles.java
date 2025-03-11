package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; 

@Entity
@Table(name = "CommunityMediaFiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityMediaFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "media_id")
    private Long mediaId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference("post-media")
    private CommunityPost post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    @JsonBackReference("comment-media")
    private CommunityComment comment;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}