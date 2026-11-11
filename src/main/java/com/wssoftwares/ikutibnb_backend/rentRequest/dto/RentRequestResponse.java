package com.wssoftwares.ikutibnb_backend.rentRequest.dto;

import com.wssoftwares.ikutibnb_backend.house.dto.RoomResponse;
import java.util.UUID;
import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;

public class RentRequestResponse {

    private UUID id;
    private UUID roomId;
    private UUID houseOwnerId;
    private RoomResponse room;
    private Long daysRemaining;
    private String endDate;

    public RentRequestResponse(RentRequest r) {
        this.id = r.getId();
        this.roomId = r.getRoom() != null ? r.getRoom().getRoomId() : null;
        this.houseOwnerId = r.getHouseOwner() != null ? r.getHouseOwner().getId() : null;
        this.room = r.getRoom() != null ? new RoomResponse(r.getRoom()) : null;
        this.daysRemaining = r.getDaysRemaining();
        this.endDate = r.getEndDate();
    }

    public UUID getId() {
        return id;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public UUID getHouseOwnerId() {
        return houseOwnerId;
    }

    public RoomResponse getRoom() {
        return room;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public String getEndDate() {
        return endDate;
    }
}
