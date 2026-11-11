package com.wssoftwares.ikutibnb_backend.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wssoftwares.ikutibnb_backend.house.entity.House;
import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;
import com.wssoftwares.ikutibnb_backend.users.dto.Review;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    private String fullname;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;
    private String profilepicture;
    private String phonenumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Review> review;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "hostId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<House> houses = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RentRequest> rentRequestsMade = new ArrayList<>();

    @OneToMany(mappedBy = "houseOwner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RentRequest> rentRequestsReceived = new ArrayList<>();

    @OneToMany(mappedBy = "agentId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<House> housesRegistered = new ArrayList<>();

    public User() {
    }

    public User(UUID id, String fullname, String email, String password,
            String profilepicture, String phonenumber, Role role) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.profilepicture = profilepicture;
        this.phonenumber = phonenumber;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<Review> getReview() { return review; }

    public void setReview(List<Review> review) { this.review = review; }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<House> getHouses() {
        return houses;
    }

    public void setHouses(List<House> houses) {
        this.houses = houses;
    }
}
