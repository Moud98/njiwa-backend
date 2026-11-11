package com.wssoftwares.ikutibnb_backend.house.dto;

import java.util.List;

public class AddHouseImagesRequest {
    private List<HouseImageRequest> images;

    public List<HouseImageRequest> getImages() {
        return images;
    }

    public void setImages(List<HouseImageRequest> images) {
        this.images = images;
    }
}
