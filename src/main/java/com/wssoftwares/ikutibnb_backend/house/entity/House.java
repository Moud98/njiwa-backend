package com.wssoftwares.ikutibnb_backend.house.entity;

import com.wssoftwares.ikutibnb_backend.users.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
public class House {
    @Id
    @UuidGenerator
    private UUID houseId;
    private String location;

    @Column(nullable = true)
    private BigDecimal price;
    
    private String houseNumber;
    private String houseName;
    private Long paymentDuration;
    private String imageUrl;
    private BigDecimal rating;
    private BigDecimal reviews;

    @Column(nullable = false)
    private int totalRooms = 0;

    @Column(columnDefinition = "TEXT")
    private String houseDescription;

    @ElementCollection
    @OrderColumn(name = "coord_index")
    @Column(precision = 20, scale = 8)
    private List<BigDecimal> coordinates = new ArrayList<>();

    @ElementCollection
    private Set<String> documents = new HashSet<>();

    @ElementCollection
    private Set<String> houseServices = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "house_payment_methods", joinColumns = @JoinColumn(name = "house_id"))
    private Set<PaymentMethod> paymentMethods = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private HouseType houseType;

    @Enumerated(EnumType.STRING)
    private HouseState state = HouseState.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User hostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = true)
    private User agentId;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HouseImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Room> rooms = new ArrayList<>();

    public UUID getHouseId() {
        return houseId;
    }

    public void setHouseId(UUID houseId) {
        this.houseId = houseId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public Long getPaymentDuration() {
        return paymentDuration;
    }

    public void setPaymentDuration(Long paymentDuration) {
        this.paymentDuration = paymentDuration;
    }

    public String getHouseDescription() {
        return houseDescription;
    }

    public void setHouseDescription(String houseDescription) {
        this.houseDescription = houseDescription;
    }

    public Set<String> getHouseServices() {
        return houseServices;
    }

    public void setHouseServices(Set<String> houseServices) {
        this.houseServices = houseServices;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<BigDecimal> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<BigDecimal> coordinates) {
        this.coordinates = coordinates;
    }

    public Set<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(Set<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Set<String> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<String> documents) {
        this.documents = documents;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    public HouseState getState() {
        return state;
    }

    public void setState(HouseState state) {
        this.state = state;
    }

    public User getHostId() {
        return hostId;
    }

    public void setHostId(User hostId) {
        this.hostId = hostId;
    }

    public User getAgentId() {
        return agentId;
    }

    public void setAgentId(User agentId) {
        this.agentId = agentId;
    }

    public List<HouseImage> getImages() {
        return images;
    }

    public void setImages(List<HouseImage> images) {
        this.images = images;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public BigDecimal getReviews() {
        return reviews;
    }

    public void setReviews(BigDecimal reviews) {
        this.reviews = reviews;
    }

    public int getTotalRooms() { return totalRooms; }

    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
}
