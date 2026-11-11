package com.wssoftwares.ikutibnb_backend.house.dto;

import com.wssoftwares.ikutibnb_backend.house.entity.PaymentMethod;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class UpdateHouse {
    private String location;
    private String houseNumber;
    private String houseName;
    private Long paymentDuration;
    private String houseDescription;
    private Set<String> houseServices;
    private String imageUrl; 
    private List<BigDecimal> coordinates;
    private Set<PaymentMethod> paymentMethods;
    private Set<String> documents;
    private BigDecimal rating;
    private BigDecimal reviews;

    
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
