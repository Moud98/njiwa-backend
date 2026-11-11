package com.wssoftwares.ikutibnb_backend.rentRequest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


public class UpdateTimerDto {

    @NotNull
    @Min(0)
    private Long days;

    @NotNull
    private String endDate;

    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
