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
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.pregnancy_tracking.repository.GrowthChartShareRepository;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final UserRepository userRepository;
    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final CommunityMediaFileRepository mediaFileRepository;
    private final GrowthChartShareRepository growthChartShareRepository;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(CommunityService.class);

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

        mediaFileRepository.deleteAll(post.getMediaFiles());

        postRepository.delete(post);
    }

    public void deleteComment(Long commentId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        mediaFileRepository.deleteAll(comment.getMediaFiles());

        commentRepository.delete(comment);
    }

    @Transactional
    public List<Map<String, Object>> getAllPostsWithCharts() {
        List<CommunityPost> posts = postRepository.findAll();
        return posts.stream().map(post -> {
            Map<String, Object> postData = new HashMap<>();
            postData.put("postId", post.getPostId());
            postData.put("title", post.getTitle());
            postData.put("content", post.getContent());
            postData.put("author", post.getAuthor());
            postData.put("createdAt", post.getCreatedAt());
            postData.put("mediaFiles", post.getMediaFiles());
            postData.put("comments", post.getComments());
            
            Optional<GrowthChartShare> chartShare = growthChartShareRepository.findByPostPostId(post.getPostId());
            if (chartShare.isPresent()) {
                try {
                    GrowthChartShare share = chartShare.get();
                    Map<String, Object> chartData = objectMapper.readValue(share.getChartData(), Map.class);
                    postData.put("chartData", chartData);
                    postData.put("sharedTypes", share.getSharedTypes());
                    postData.put("postType", "GROWTH_CHART");
                    postData.put("fetusId", share.getFetus().getFetusId());
                } catch (JsonProcessingException e) {
                    log.error("Error parsing chart data for post {}: {}", post.getPostId(), e.getMessage());
                }
            }
            
            return postData;
        }).collect(Collectors.toList());
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