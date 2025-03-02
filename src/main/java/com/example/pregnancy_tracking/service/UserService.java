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

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public String register(RegisterRequest request) {
        // Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.GUEST);

        userRepository.save(user);
        return "User registered successfully!";
    }

    public AuthResponse login(LoginRequest request) {
        System.out.println("Login attempt with email: " + request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("Found user: " + user.getEmail());
        System.out.println("Stored password hash: " + user.getPassword());
        System.out.println("Input password: " + request.getPassword());

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("Password matches: " + matches);

        if (!matches) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public void changePassword(String email, ChangePasswordRequest request) { // Đổi từ username thành email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user, user.getUserProfile()))
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
        return new UserDTO(user, user.getUserProfile());
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));

        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUser(user);
            user.setUserProfile(userProfile);
        }

        // Cập nhật thông tin cơ bản
        if (userDTO.getUsername() != null)
            user.setUsername(userDTO.getUsername());
        if (userDTO.getEmail() != null)
            user.setEmail(userDTO.getEmail());

        // Cập nhật thông tin profile
        if (userDTO.getFullName() != null)
            userProfile.setFullName(userDTO.getFullName());
        if (userDTO.getPhoneNumber() != null)
            userProfile.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getAvatar() != null)
            userProfile.setAvatar(userDTO.getAvatar());

        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser, savedUser.getUserProfile());
    }
}
