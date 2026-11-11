package com.wssoftwares.ikutibnb_backend.rentRequest.dto;

import com.wssoftwares.ikutibnb_backend.house.dto.RoomResponse;
import java.util.UUID;

public class ApprovedRoomResponse {

    private UUID rentRequestId;
    private String roomName;
    private RoomResponse room;
    private Long daysRemaining;
    private String endDate;

    public ApprovedRoomResponse() {
    }

    public ApprovedRoomResponse(UUID rentRequestId, RoomResponse room, Long daysRemaining, String endDate) {
        this.rentRequestId = rentRequestId;
        this.roomName = room != null ? room.getRoomName() : null;
        this.room = room;
        this.daysRemaining = daysRemaining;
        this.endDate = endDate;
    }

    public UUID getRentRequestId() {
        return rentRequestId;
    }

    public void setRentRequestId(UUID rentRequestId) {
        this.rentRequestId = rentRequestId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public RoomResponse getRoom() {
        return room;
    }

    public void setRoom(RoomResponse room) {
        this.room = room;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
