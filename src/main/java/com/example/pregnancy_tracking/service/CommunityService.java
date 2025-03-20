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
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));

        CommunityPost post = new CommunityPost();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);
        post.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);
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
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));

        mediaFileRepository.deleteAll(post.getMediaFiles());

        postRepository.delete(post);
    }

    public void deleteComment(Long commentId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận!"));

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
            postData.put("isAnonymous", post.getIsAnonymous());
            postData.put("createdAt", post.getCreatedAt());
            postData.put("mediaFiles", post.getMediaFiles());
            postData.put("comments", post.getComments());
            
            try {
                Optional<GrowthChartShare> chartShare = growthChartShareRepository.findByPostPostId(post.getPostId());
                if (chartShare.isPresent()) {
                    GrowthChartShare share = chartShare.get();
                    String chartDataStr = share.getChartData();
                    if (chartDataStr != null && !chartDataStr.isEmpty()) {
                        try {
                            Map<String, Object> chartData = objectMapper.readValue(chartDataStr, Map.class);
                            postData.put("chartData", chartData);
                            postData.put("sharedTypes", share.getSharedTypes());
                            postData.put("postType", "GROWTH_CHART");
                            if (share.getFetus() != null) {
                                postData.put("fetusId", share.getFetus().getFetusId());
                            }
                        } catch (JsonProcessingException e) {
                            log.error("Error parsing chart data JSON for post {}: {}", post.getPostId(), e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error processing growth chart share for post {}: {}", post.getPostId(), e.getMessage(), e);
            }
            
            return postData;
        }).collect(Collectors.toList());
    }

    public List<CommunityComment> getCommentsByPostId(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));
        return post.getComments();
    }

    @Transactional
    public Map<String, Object> getPostByIdWithCharts(Long postId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết!"));

        Map<String, Object> postData = new HashMap<>();
        postData.put("postId", post.getPostId());
        postData.put("title", post.getTitle());
        postData.put("content", post.getContent());
        postData.put("author", post.getAuthor());
        postData.put("isAnonymous", post.getIsAnonymous());
        postData.put("createdAt", post.getCreatedAt());
        postData.put("mediaFiles", post.getMediaFiles());
        postData.put("comments", post.getComments());
        
        try {
            Optional<GrowthChartShare> chartShare = growthChartShareRepository.findByPostPostId(post.getPostId());
            if (chartShare.isPresent()) {
                GrowthChartShare share = chartShare.get();
                String chartDataStr = share.getChartData();
                if (chartDataStr != null && !chartDataStr.isEmpty()) {
                    try {
                        Map<String, Object> chartData = objectMapper.readValue(chartDataStr, Map.class);
                        postData.put("chartData", chartData);
                        postData.put("sharedTypes", share.getSharedTypes());
                        postData.put("postType", "GROWTH_CHART");
                        if (share.getFetus() != null) {
                            postData.put("fetusId", share.getFetus().getFetusId());
                        }
                    } catch (JsonProcessingException e) {
                        log.error("Error parsing chart data JSON for post {}: {}", post.getPostId(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing growth chart share for post {}: {}", post.getPostId(), e.getMessage(), e);
        }
        
        return postData;
    }
    
    @Transactional
    public void deleteUserContent(Long userId) {
        // Xóa media files của comments và posts
        List<CommunityComment> userComments = commentRepository.findByAuthorId(userId);
        List<CommunityPost> userPosts = postRepository.findByAuthorId(userId);
        
        userComments.forEach(comment -> 
            mediaFileRepository.deleteByCommentCommentId(comment.getCommentId()));
            
        userPosts.forEach(post -> 
            mediaFileRepository.deleteByPostPostId(post.getPostId()));
        
        // Xóa growth chart shares
        growthChartShareRepository.deleteByPostAuthorId(userId);
    
        
        // Xóa comments
        commentRepository.deleteByAuthorId(userId);
        
        // Xóa posts
        postRepository.deleteByAuthorId(userId);
    }
}