package com.wssoftwares.ikutibnb_backend.auth.dto;

import java.util.List;
import java.util.UUID;

public class LoginResponse {
    private String accessToken;
    private UserInfo user;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken, UserInfo user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public static class UserInfo {
        private UUID id;
        private String email;
        private Role role;
        private String fullname;
        private String profilePicture;
        private String phonenumber;

        public UserInfo() {
        }

        public UserInfo(UUID id, String email, Role role, String fullname, String profilePicture, String phonenumber) {
            this.id = id;
            this.email = email;
            this.role = role;
            this.fullname = fullname;
            this.profilePicture = profilePicture;
            this.phonenumber = phonenumber;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getPhonenumber() {
            return phonenumber;
        }

        public void setPhonenumber(String phonenumber) {
            this.phonenumber = phonenumber;
        }
    }

    public static class Role {
        private String name;
        private List<String> permissions;

        public Role() {
        }

        public Role(String name, List<String> permissions) {
            this.name = name;
            this.permissions = permissions;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }

}
