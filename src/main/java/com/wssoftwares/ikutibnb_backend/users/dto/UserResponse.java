package com.wssoftwares.ikutibnb_backend.users.dto;

import com.wssoftwares.ikutibnb_backend.users.entity.Role;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import java.util.UUID;
import java.util.List;

public class UserResponse {
    private UUID id;
    private String fullname;
    private String email;
    private String profilepicture;
    private String phonenumber;
    private Role role;
    private List<Review> review;

    public UserResponse() {
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.profilepicture = user.getProfilepicture();
        this.phonenumber = user.getPhonenumber();
        this.role = user.getRole();
        this.review = user.getReview();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Review> getReview() { return review; }

    public void setReview(List<Review> review) { this.review = review; }
}
