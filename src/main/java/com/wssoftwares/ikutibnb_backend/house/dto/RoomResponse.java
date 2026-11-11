package com.wssoftwares.ikutibnb_backend.house.dto;

import com.wssoftwares.ikutibnb_backend.house.entity.Room;
import com.wssoftwares.ikutibnb_backend.house.entity.RoomStatus;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RoomResponse {
    private UUID roomId;
    private UUID houseId;
    private String roomName;
    private Long paymentDuration;
    private BigDecimal price;
    private String houseType;
    private UUID clientId;
    private String clientProfilepicture;
    private String clientName;
    private String clientPhone;
    private RoomStatus status;
    private List<ImageResponse> images;

    public RoomResponse(Room room) {
        roomId = room.getRoomId();
        houseId = room.getHouse().getHouseId();
        roomName = room.getRoomName();
        paymentDuration = room.getPaymentDuration();
        price = room.getPrice();
        houseType = room.getHouseType().name();
        clientId = room.getClientId() == null ? null : room.getClientId().getId();
        clientProfilepicture = room.getClientId() == null ? null : room.getClientId().getProfilepicture();
        clientName = room.getClientName();
        clientPhone = room.getClientPhone();
        status = room.getStatus();
        images = room.getImages() == null ? Collections.emptyList()
                : room.getImages().stream()
                        .map(image -> new ImageResponse(image.getId(), image.getImageUrl(), image.getCategory()))
                        .toList();
    }

    public UUID getRoomId() {
        return roomId;
    }

    public UUID getHouseId() {
        return houseId;
    }

    public String getRoomName() {
        return roomName;
    }

    public Long getPaymentDuration() { return paymentDuration; }

    public BigDecimal getPrice() {
        return price;
    }

    public String getHouseType() {
        return houseType;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getClientProfilepicture() {
        return clientProfilepicture;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public List<ImageResponse> getImages() {
        return images;
    }
}
