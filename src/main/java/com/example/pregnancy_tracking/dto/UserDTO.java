package com.example.pregnancy_tracking.dto;

import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.entity.UserProfile;
import com.example.pregnancy_tracking.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String avatar;
    private Role role;

    public UserDTO(User user, UserProfile userProfile) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsernameField();
        this.role = user.getRole();
        if (userProfile != null) {
            this.fullName = userProfile.getFullName();
            this.phoneNumber = userProfile.getPhoneNumber();
            this.avatar = userProfile.getAvatar();
        }
    }
}
