package com.wssoftwares.ikutibnb_backend.house.dto;

import jakarta.validation.constraints.*;
import com.wssoftwares.ikutibnb_backend.house.entity.PaymentMethod;
import java.util.List;
import java.util.Set;
import java.math.BigDecimal;
import java.util.UUID;

public class CreateHouse {
    @NotBlank
    private String location;

    private BigDecimal rating;

    private BigDecimal reviews;

    private String houseNumber;

    private String houseName;

    @NotNull
    private Long paymentDuration;

    @NotBlank
    private String houseDescription;

    private Set<String> houseServices;

    private String imageUrl;

    // When supplied, the first image becomes the house's primary imageUrl;
    // the remaining images are stored in the house image collection.
    private List<HouseImageRequest> images;

    private List<BigDecimal> coordinates;

    private Set<String> documents;

    private Set<PaymentMethod> paymentMethods;

    private UUID hostId;

    private UUID agentId;

    public UUID getAgentId() {
        return agentId;
    }

    public void setAgentId(UUID agentId) {
        this.agentId = agentId;
    }

    public UUID getHostId() {
        return hostId;
    }

    public void setHostId(UUID hostId) {
        this.hostId = hostId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public List<HouseImageRequest> getImages() {
        return images;
    }

    public void setImages(List<HouseImageRequest> images) {
        this.images = images;
    }

    public List<BigDecimal> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<BigDecimal> coordinates) {
        this.coordinates = coordinates;
    }

    public Set<String> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<String> documents) {
        this.documents = documents;
    }

    public Set<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(Set<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
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
