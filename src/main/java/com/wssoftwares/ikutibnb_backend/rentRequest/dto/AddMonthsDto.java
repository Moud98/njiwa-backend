package com.wssoftwares.ikutibnb_backend.rentRequest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddMonthsDto {

    @NotNull
    @Min(1)
    private Integer months;

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }
}
