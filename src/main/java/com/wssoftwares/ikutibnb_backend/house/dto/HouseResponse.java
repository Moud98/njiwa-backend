package com.wssoftwares.ikutibnb_backend.house.dto;

import com.wssoftwares.ikutibnb_backend.house.entity.House;
import com.wssoftwares.ikutibnb_backend.house.entity.PaymentMethod;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

public class HouseResponse {

    private UUID houseId;
    private String location;
    private String houseNumber;
    private String houseName;
    private BigDecimal rating;
    private BigDecimal reviews;
    private int totalRooms;
    private Long paymentDuration;
    private String houseDescription;
    private Set<String> houseServices;
    private String imageUrl;
    private List<BigDecimal> coordinates;
    private Set<PaymentMethod> paymentMethods;
    private Set<String> documents;
    private UserResponse hostId;
    private UserResponse agentId;

    private List<ImageResponse> images;
    private List<RoomResponse> rooms;

    public HouseResponse() {
    }

    public HouseResponse(House house) {

        this.houseId = house.getHouseId();
        this.location = house.getLocation();
        this.houseNumber = house.getHouseNumber();
        this.houseName = house.getHouseName();

        this.paymentDuration = house.getPaymentDuration();
        this.houseDescription = house.getHouseDescription();
        this.imageUrl = house.getImageUrl();
        this.rating = house.getRating();
        this.reviews = house.getReviews();
        this.totalRooms = house.getTotalRooms();

        // NULL-SAFE Set mappings
        this.houseServices = house.getHouseServices() != null
                ? house.getHouseServices()
                : Collections.emptySet();

        this.coordinates = house.getCoordinates() != null
                ? house.getCoordinates()
                : Collections.emptyList();

        this.paymentMethods = house.getPaymentMethods() != null
                ? house.getPaymentMethods()
                : Collections.emptySet();

        this.documents = house.getDocuments() != null
                ? house.getDocuments()
                : Collections.emptySet();

        this.hostId = house.getHostId() != null
                ? new UserResponse(house.getHostId())
                : null;

        this.agentId = house.getAgentId() != null
                ? new UserResponse(house.getAgentId())
                : null;

        // NULL-SAFE image mapping
        this.images = house.getImages() != null
                ? house.getImages()
                        .stream()
                        .map(img -> new ImageResponse(
                                img.getImageUrl(),
                                img.getCategory()))
                        .collect(Collectors.toList())
                : Collections.emptyList();

        this.rooms = house.getRooms() != null
                ? house.getRooms().stream()
                        .map(RoomResponse::new)
                        .collect(Collectors.toList())
                : Collections.emptyList();
    }

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

    public UserResponse getHostId() {
        return hostId;
    }

    public void setHostId(UserResponse hostId) {
        this.hostId = hostId;
    }

    public UserResponse getAgentId() {
        return agentId;
    }

    public void setAgentId(UserResponse agentId) {
        this.agentId = agentId;
    }

    public List<ImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ImageResponse> images) {
        this.images = images;
    }

    public List<RoomResponse> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomResponse> rooms) {
        this.rooms = rooms;
    }
}
