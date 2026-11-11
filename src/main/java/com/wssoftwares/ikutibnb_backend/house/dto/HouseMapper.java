package com.wssoftwares.ikutibnb_backend.house.dto;

import com.wssoftwares.ikutibnb_backend.house.entity.House;
import com.wssoftwares.ikutibnb_backend.users.entity.User;

import java.util.stream.Collectors;

public class HouseMapper {
    public static HouseDTO toDTO(House house) {
        HouseDTO dto = new HouseDTO();
        dto.setHouseId(house.getHouseId());
        dto.setLocation(house.getLocation());
        dto.setPrice(house.getPrice());
        dto.setHouseNumber(house.getHouseNumber());
        dto.setHouseName(house.getHouseName());
        dto.setHouseType(house.getHouseType());
        dto.setState(house.getState());
        dto.setPaymentDuration(house.getPaymentDuration());
        dto.setHouseDescription(house.getHouseDescription());
        dto.setHouseServices(house.getHouseServices());
        dto.setImageUrl(house.getImageUrl());
        dto.setCoordinates(house.getCoordinates());
        dto.setPaymentMethods(house.getPaymentMethods());
        dto.setDocuments(house.getDocuments());
        dto.setRating(house.getRating());
        dto.setReviews(house.getReviews());

        // Map host info
        User host = house.getHostId();
        UserDTO hostDTO = new UserDTO();
        hostDTO.setId(host.getId());
        hostDTO.setFullname(host.getFullname());
        hostDTO.setEmail(host.getEmail());
        hostDTO.setProfilepicture(host.getProfilepicture());
        hostDTO.setPhonenumber(host.getPhonenumber());
        hostDTO.setRole(host.getRole().name());
        dto.setHostId(hostDTO);

        // UPDATED: Map images with category
        dto.setImages(house.getImages().stream()
                .map(img -> new ImageResponse(img.getImageUrl(), img.getCategory()))
                .collect(Collectors.toList()));

        return dto;
    }
}
