package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.CommentRequest;
import com.example.pregnancy_tracking.dto.PostRequest;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.CommunityCommentRepository;
import com.example.pregnancy_tracking.repository.CommunityPostRepository;
import com.example.pregnancy_tracking.repository.CommunityMediaFileRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final UserRepository userRepository;
    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityMediaFileRepository mediaFileRepository;

    public CommunityPost createPost(PostRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommunityPost post = new CommunityPost();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);
        post.setIsAnonymous(request.getIsAnonymous());
        post.setCreatedAt(LocalDateTime.now());
        
        post = postRepository.save(post);

        if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
            for (String mediaUrl : request.getMediaUrls()) {
                CommunityMediaFiles mediaFile = new CommunityMediaFiles();
                mediaFile.setPost(post);
                mediaFile.setMediaUrl(mediaUrl);
                mediaFile.setUploadedAt(LocalDateTime.now());
                mediaFileRepository.save(mediaFile);
            }
        }

        return post;
    }

    public CommunityComment createComment(Long postId, CommentRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        CommunityComment comment = new CommunityComment();
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setContent(request.getContent());
        comment.setIsAnonymous(request.getIsAnonymous());
        comment.setCreatedAt(LocalDateTime.now());
        
        comment = commentRepository.save(comment);

        if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
            for (String mediaUrl : request.getMediaUrls()) {
                CommunityMediaFiles mediaFile = new CommunityMediaFiles();
                mediaFile.setComment(comment);
                mediaFile.setMediaUrl(mediaUrl);
                mediaFile.setUploadedAt(LocalDateTime.now());
                mediaFileRepository.save(mediaFile);
            }
        }

        return comment;
    }

    public void deletePost(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Delete associated media files first
        mediaFileRepository.deleteAll(post.getMediaFiles());
        
        // Delete the post (this will cascade delete comments due to FK constraint)
        postRepository.delete(post);
    }

    public void deleteComment(Long commentId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Delete associated media files first
        mediaFileRepository.deleteAll(comment.getMediaFiles());
        
        // Delete the comment
        commentRepository.delete(comment);
    }

    public List<CommunityPost> getAllPosts() {
        return postRepository.findAll();
    }

    public List<CommunityComment> getCommentsByPostId(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return post.getComments();
    }

    public CommunityPost getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }
}