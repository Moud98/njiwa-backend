package com.wssoftwares.ikutibnb_backend.rentRequest.dto;

import com.wssoftwares.ikutibnb_backend.house.entity.RoomStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateRentRequestStatusDto {

    @NotNull
    private RoomStatus status;

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
