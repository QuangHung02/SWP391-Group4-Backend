package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.UserDTO;
import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.entity.UserProfile;
import com.example.pregnancy_tracking.exception.UserNotFoundException;
import com.example.pregnancy_tracking.repository.UserProfileRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserService(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return convertToDTO(user);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(new UserProfile());
        userProfile.setUser(user);
        userProfile.setFullName(userDTO.getFullName());
        userProfile.setPhoneNumber(userDTO.getPhoneNumber());
        userProfile.setAvatar(userDTO.getAvatar());

        userRepository.save(user);
        userProfileRepository.save(userProfile);

        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(null);
        return new UserDTO(user, userProfile);
    }
}
