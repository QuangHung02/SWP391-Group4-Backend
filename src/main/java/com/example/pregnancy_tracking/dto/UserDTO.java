package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.entity.UserProfile;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatar;

    public UserDTO() {}

    public UserDTO(User user, UserProfile userProfile) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        if (userProfile != null) {
            this.fullName = userProfile.getFullName();
            this.phoneNumber = userProfile.getPhoneNumber();
            this.avatar = userProfile.getAvatar();
        }
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
