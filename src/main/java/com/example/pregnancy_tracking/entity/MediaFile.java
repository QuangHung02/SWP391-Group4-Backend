package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "MediaFiles")
@Data
public class MediaFile {
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

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}