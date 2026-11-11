package com.wssoftwares.ikutibnb_backend.users.dto;

import com.wssoftwares.ikutibnb_backend.users.entity.Role;

import jakarta.validation.constraints.*;

public class CreateUser {

    @NotBlank
    private String fullname;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    @NotBlank
    private String password;

    private String profilepicture;

    private String phonenumber;

    @NotNull
    private Role role;

    public CreateUser() {}

    public CreateUser(String fullname, String email, String password,
                      String profilepicture, String phonenumber, Role role) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
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
}
