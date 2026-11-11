package com.wssoftwares.ikutibnb_backend.house;

import com.wssoftwares.ikutibnb_backend.house.entity.House;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import com.wssoftwares.ikutibnb_backend.house.entity.HouseType;
import com.wssoftwares.ikutibnb_backend.house.dto.AddHouseImagesRequest;
import com.wssoftwares.ikutibnb_backend.house.dto.CreateHouse;
import com.wssoftwares.ikutibnb_backend.house.dto.UpdateHouse;
import com.wssoftwares.ikutibnb_backend.house.entity.HouseImage;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseImageRequest;
import org.springframework.security.core.Authentication;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface HouseService {

    House CreateHouse(CreateHouse dto, Authentication authentication);

    Page<House> getAllHouses(int page, int size, String location, String price, HouseType houseType,
            Authentication authentication); 
            
    // Page<House> getAllHousesClient(String location, String price, HouseType houseType,
    //         Authentication authentication);         

    Page<House> getHousesByHost(int page, int size, String houseName, String fullname,String location, Authentication authentication);

    House getHouseById(UUID houseId);

    List<HouseImage> getImagesByCategory(UUID houseId, String category);

    House updateHouse(UUID houseId, UpdateHouse dto, Authentication authentication);

    void deleteHouse(UUID houseId, Authentication authentication);

    void addImagesToHouse(UUID houseId, List<HouseImageRequest> images, Authentication authentication);

    void deleteHouseImages(List<UUID> imageIds, Authentication authentication);

    List<HouseDTO> searchHouses(String location, String price, HouseType houseType, Authentication authentication);

}
