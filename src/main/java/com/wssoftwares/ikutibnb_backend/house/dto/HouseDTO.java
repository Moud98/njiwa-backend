package com.wssoftwares.ikutibnb_backend.house.dto;

import com.wssoftwares.ikutibnb_backend.house.entity.HouseState;
import com.wssoftwares.ikutibnb_backend.house.entity.HouseType;
import com.wssoftwares.ikutibnb_backend.house.entity.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HouseDTO {
    private UUID houseId;
    private String location;
    private BigDecimal price;
    private String houseNumber;
    private String houseName;
    private HouseType houseType;
    private HouseState state;
    private Long paymentDuration;
    private String houseDescription;
    private Set<String> houseServices;
    private String imageUrl;
    private List<BigDecimal> coordinates;
    private Set<PaymentMethod> paymentMethods;
    private Set<String> documents;
    private BigDecimal rating;
    private BigDecimal reviews;
    private UserDTO hostId;
    private UserDTO agentId;

    // UPDATED: now holds objects with imageUrl + category
    private List<ImageResponse> images;

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

    public Long getPaymentDuration() {
        return paymentDuration;
    }

    public void setPaymentDuration(Long paymentDuration) {
        this.paymentDuration = paymentDuration;
    }

    public UserDTO getHostId() {
        return hostId;
    }

    public void setHostId(UserDTO hostId) {
        this.hostId = hostId;
    }

    public UserDTO getAgentId() {
        return agentId;
    }

    public void setAgentId(UserDTO agentId) {
        this.agentId = agentId;
    }

    public List<ImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ImageResponse> images) {
        this.images = images;
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
}
