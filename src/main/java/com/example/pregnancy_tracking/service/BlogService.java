package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.BlogRequest;
import com.example.pregnancy_tracking.entity.Blog;
import com.example.pregnancy_tracking.entity.BlogImage;  
import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.repository.BlogRepository;
import com.example.pregnancy_tracking.repository.BlogImageRepository;  
import com.example.pregnancy_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;
    private final BlogImageRepository blogImageRepository;
    private final UserRepository userRepository;

    public Blog createBlog(BlogRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setAuthor(author);
        blog.setCreatedAt(LocalDateTime.now());
        
        blog = blogRepository.save(blog);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                BlogImage image = new BlogImage();
                image.setBlog(blog);
                image.setImageUrl(imageUrl);
                image.setCreatedAt(LocalDateTime.now());
                blogImageRepository.save(image);
            }
        }

        return blog;
    }

    public Blog updateBlog(Long blogId, BlogRequest request) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        
        // Update images
        blog.getImages().clear();
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (String imageUrl : request.getImageUrls()) {
                BlogImage image = new BlogImage();
                image.setBlog(blog);
                image.setImageUrl(imageUrl);
                image.setCreatedAt(LocalDateTime.now());
                blog.getImages().add(image);
            }
        }
        
        return blogRepository.save(blog);
    }

    public void deleteBlog(Long blogId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        blogRepository.delete(blog);
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Blog getBlogById(Long blogId) {
        return blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
    }
}