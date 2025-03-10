package com.example.pregnancy_tracking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "CommunityComments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference("post-comments")
    private CommunityPost post;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    @JsonManagedReference("comment-media")
    @ToString.Exclude
    private List<MediaFile> mediaFiles;
}