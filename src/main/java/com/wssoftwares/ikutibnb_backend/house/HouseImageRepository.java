package com.wssoftwares.ikutibnb_backend.house;

import com.wssoftwares.ikutibnb_backend.house.entity.HouseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HouseImageRepository extends JpaRepository<HouseImage, UUID> {
    List<HouseImage> findByHouseHouseIdAndCategory(UUID houseId, String category);
}