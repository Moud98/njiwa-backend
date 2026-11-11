package com.wssoftwares.ikutibnb_backend.users.dto;

import com.wssoftwares.ikutibnb_backend.users.entity.Role;
import java.util.List;



public class UpdateUser {
    private String fullname;
    private String email;
    private String profilepicture;
    private String phonenumber;
    private Role role;
    private List<Review> review;



    public UpdateUser() {}

    public UpdateUser(String fullname, String email, String profilepicture,
                      String phonenumber, Role role) {
        this.fullname = fullname;
        this.email = email;
        this.profilepicture = profilepicture;
        this.phonenumber = phonenumber;
        this.role = role;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public Role getRole() {
        return role;
    }

    public List<Review> getReview() {
        return review;
    }

}
