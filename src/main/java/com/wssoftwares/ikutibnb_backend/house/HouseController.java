package com.wssoftwares.ikutibnb_backend.house;

import com.wssoftwares.ikutibnb_backend.house.dto.AddHouseImagesRequest;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseImageRequest;
import com.wssoftwares.ikutibnb_backend.house.dto.CreateHouse;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseDTO;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseResponse;
import com.wssoftwares.ikutibnb_backend.house.dto.UpdateHouse;
import com.wssoftwares.ikutibnb_backend.house.dto.ImageResponse;
import com.wssoftwares.ikutibnb_backend.house.entity.House;
import com.wssoftwares.ikutibnb_backend.house.entity.HouseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/house")
public class HouseController {

    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN') or hasRole('HOST')")
    public ResponseEntity<HouseResponse> createHouse(@Valid @RequestBody CreateHouse dto,
            Authentication authentication) {
        House createdHouse = houseService.CreateHouse(dto, authentication);
        return ResponseEntity.ok(new HouseResponse(createdHouse));
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllHouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) HouseType houseType,
            Authentication authentication) {

        Page<House> houses = houseService.getAllHouses(
                page, size, location, price, houseType, authentication);

        List<HouseResponse> houseResponses = houses.getContent()
                .stream()
                .map(HouseResponse::new)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("houses", houseResponses);
        response.put("totalElements", houses.getTotalElements());
        response.put("totalPages", houses.getTotalPages());
        response.put("currentPage", houses.getNumber() + 1);
        response.put("pageSize", houses.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllHousesForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) HouseType houseType,
            Authentication authentication) {

        Page<House> houses = houseService.getAllHouses(
                page, size, location, price, houseType, authentication);

        List<HouseResponse> houseResponses = houses.getContent()
                .stream()
                .map(HouseResponse::new)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("houses", houseResponses);
        response.put("totalPages", houses.getTotalPages());
        response.put("totalElements", houses.getTotalElements());
        response.put("currentPage", houses.getNumber() + 1);
        response.put("pageSize", houses.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('HOST','AGENT','ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyHouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String houseName,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String location,
            Authentication authentication) {

        Page<House> houses = houseService.getHousesByHost(page, size, houseName, fullname, location, authentication);

        List<HouseResponse> houseResponses = houses.getContent()
                .stream()
                .map(HouseResponse::new)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("houses", houseResponses);
        response.put("totalPages", houses.getTotalPages());
        response.put("totalElements", houses.getTotalElements());
        response.put("currentPage", houses.getNumber() + 1);
        response.put("pageSize", houses.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my/nopagination")
    @PreAuthorize("hasAnyRole('HOST','AGENT','ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyHousesNoPagination(
            @RequestParam(required = false) String houseName,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String location,
            Authentication authentication) {

        Page<House> houses = houseService.getHousesByHost(
                0, Integer.MAX_VALUE, houseName, fullname, location, authentication);

        List<HouseResponse> houseResponses = houses.getContent()
                .stream()
                .map(HouseResponse::new)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("houses", houseResponses);
        response.put("totalElements", houseResponses.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by/{houseId}")
    public ResponseEntity<HouseResponse> getHouseById(@PathVariable UUID houseId) {
        House house = houseService.getHouseById(houseId);
        return ResponseEntity.ok(new HouseResponse(house));
    }

    @GetMapping("/images/{houseId}/{category}")
    public ResponseEntity<List<ImageResponse>> getImagesByCategory(
            @PathVariable UUID houseId,
            @PathVariable String category) {

        List<ImageResponse> images = houseService.getImagesByCategory(houseId, category)
                .stream()
                .map(img -> new ImageResponse(img.getImageUrl(), img.getCategory()))
                .toList();

        return ResponseEntity.ok(images);
    }

    @PatchMapping("/{houseId}")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<HouseResponse> updateHouse(@PathVariable UUID houseId,
            @RequestBody UpdateHouse dto,
            Authentication authentication) {
        House updatedHouse = houseService.updateHouse(houseId, dto, authentication);
        return ResponseEntity.ok(new HouseResponse(updatedHouse));
    }

    @DeleteMapping("/{houseId}")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteHouse(@PathVariable UUID houseId, Authentication authentication) {
        houseService.deleteHouse(houseId, authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "House deleted successfully");
        response.put("houseId", houseId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/images/{houseId}")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Map<String, Object>> addImagesToHouse(
            @PathVariable UUID houseId,
            @RequestBody AddHouseImagesRequest request,
            Authentication authentication) {

        List<HouseImageRequest> images = request.getImages();
        houseService.addImagesToHouse(houseId, images, authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Images uploaded successfully");
        response.put("houseId", houseId);
        response.put("uploadedCount", images.size());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("images")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Map<String, Object>> deleteHouseImages(
            @RequestBody List<UUID> imageIds,
            Authentication authentication) {

        houseService.deleteHouseImages(imageIds, authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Images deleted successfully");
        response.put("deletedImageIds", imageIds);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/houses/search")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN') or hasRole('HOST')")
    public ResponseEntity<List<HouseDTO>> searchHouses(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) HouseType houseType,
            Authentication authentication) {

        List<HouseDTO> results = houseService.searchHouses(location, price, houseType, authentication);
        return ResponseEntity.ok(results);
    }

}
