package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.AuthResponse;
import com.example.pregnancy_tracking.dto.ChangePasswordRequest;
import com.example.pregnancy_tracking.dto.LoginRequest;
import com.example.pregnancy_tracking.dto.RegisterRequest;
import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.entity.Role;
import com.example.pregnancy_tracking.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.pregnancy_tracking.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.pregnancy_tracking.dto.UserDTO;
import com.example.pregnancy_tracking.entity.UserProfile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        user.setUsername(username);
        user.setRole(Role.MEMBER);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName("");
        profile.setPhoneNumber("");
        profile.setAvatar("");

        user.setUserProfile(profile);
        
        User savedUser = userRepository.save(user);

        if (savedUser.getUserProfile() == null) {
            throw new RuntimeException("Failed to create user profile");
        }

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user, user.getId());
        return new AuthResponse(token);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        // Validate input
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new RuntimeException("New password cannot be empty");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Validate that new password is different from old password
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from old password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user, user.getUserProfile()))
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return new UserDTO(user, user.getUserProfile());
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }

        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUser(user);
            user.setUserProfile(userProfile);
        }

        if (userDTO.getFullName() != null) {
            userProfile.setFullName(userDTO.getFullName());
        }
        if (userDTO.getPhoneNumber() != null) {
            userProfile.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getAvatar() != null) {
            userProfile.setAvatar(userDTO.getAvatar());
        }

        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser, savedUser.getUserProfile());
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if trying to delete an admin
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin accounts");
        }
        
        userRepository.delete(user);
    }
}
