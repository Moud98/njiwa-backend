package com.wssoftwares.ikutibnb_backend.house.dto;

import com.wssoftwares.ikutibnb_backend.house.entity.HouseType;
import com.wssoftwares.ikutibnb_backend.house.entity.RoomStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateRoom {
    private String roomName;
    @NotNull
    private BigDecimal price;
    @NotNull
    private HouseType houseType;
    private UUID clientId;
    private RoomStatus status = RoomStatus.AVAILABLE;
    private List<HouseImageRequest> images;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public void setHouseType(HouseType houseType) {
        this.houseType = houseType;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public List<HouseImageRequest> getImages() {
        return images;
    }

    public void setImages(List<HouseImageRequest> images) {
        this.images = images;
    }
}
