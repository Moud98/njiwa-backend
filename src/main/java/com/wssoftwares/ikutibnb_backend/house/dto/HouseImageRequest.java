package com.wssoftwares.ikutibnb_backend.house.dto;

public class HouseImageRequest {
    private String base64Data;
    private String category;

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
