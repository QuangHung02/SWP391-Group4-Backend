package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.entity.UserProfile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Tự động tạo getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Tạo constructor không tham số
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatar;

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
}
