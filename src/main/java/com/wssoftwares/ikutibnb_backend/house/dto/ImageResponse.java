package com.wssoftwares.ikutibnb_backend.house.dto;

import java.util.UUID;

public class ImageResponse {
    private UUID imageId;
    private String imageUrl;
    private String category;

    
    public ImageResponse() {
    }

    public ImageResponse(String imageUrl, String category) {
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public ImageResponse(UUID imageId, String imageUrl, String category) {
        this(imageUrl, category);
        this.imageId = imageId;
    }

    public UUID getImageId() { return imageId; }
    public void setImageId(UUID imageId) { this.imageId = imageId; }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "ImageResponse{" +
                "imageUrl='" + imageUrl + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
