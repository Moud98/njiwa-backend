package com.wssoftwares.ikutibnb_backend.house.impl;

import com.wssoftwares.ikutibnb_backend.house.*;
import com.wssoftwares.ikutibnb_backend.house.dto.CreateRoom;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseImageRequest;
import com.wssoftwares.ikutibnb_backend.house.dto.UpdateRoom;
import com.wssoftwares.ikutibnb_backend.house.entity.*;
import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final ImageUtils imageUtils;

    public RoomServiceImpl(RoomRepository roomRepository, HouseRepository houseRepository,
            UserRepository userRepository, ImageUtils imageUtils) {
        this.roomRepository = roomRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.imageUtils = imageUtils;
    }

    @Override
    public Room createRoom(UUID houseId, CreateRoom dto, Authentication authentication) {
        House house = getHouse(houseId);
        verifyCanManage(house, authentication);

        Room room = new Room();
        room.setHouse(house);
        room.setRoomName(dto.getRoomName());
        room.setPaymentDuration(house.getPaymentDuration());
        room.setPrice(dto.getPrice());
        room.setHouseType(dto.getHouseType());
        RoomStatus status = dto.getStatus() == null ? RoomStatus.AVAILABLE : dto.getStatus();
        User client = resolveClient(dto.getClientId());
        if (client != null && status == RoomStatus.AVAILABLE) {
            throw new IllegalArgumentException("A room with a client cannot have AVAILABLE status");
        }
        room.setStatus(status);
        room.setClientId(client);
        room = roomRepository.save(room);
        house.setTotalRooms(house.getTotalRooms() + 1);
        houseRepository.save(house);
        addImages(room, dto.getImages());
        return roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByHouseId(UUID houseId) {
        getHouse(houseId);
        return roomRepository.findByHouseHouseIdOrderByRoomIdAsc(houseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(UUID roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
    }

    @Override
    public Room updateRoom(UUID roomId, UpdateRoom dto, Authentication authentication) {
        Room room = getRoomById(roomId);
        verifyCanManage(room.getHouse(), authentication);

        if (dto.getRoomName() != null)
            room.setRoomName(dto.getRoomName());
        if (dto.getPrice() != null)
            room.setPrice(dto.getPrice());
        if (dto.getHouseType() != null)
            room.setHouseType(dto.getHouseType());
        if (dto.getClientId() != null) {
            room.setClientId(resolveClient(dto.getClientId()));
            if (dto.getStatus() == null)
                room.setStatus(RoomStatus.APPROVED);
        }
        if (dto.getStatus() != null) {
            room.setStatus(dto.getStatus());
            if (dto.getStatus() == RoomStatus.AVAILABLE)
                room.setClientId(null);
        }
        if (dto.getImages() != null) {
            // PATCH adds images; an empty array leaves the existing collection unchanged.
            addImages(room, dto.getImages());
        }
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(UUID roomId, Authentication authentication) {
        Room room = getRoomById(roomId);
        House house = room.getHouse();
        verifyCanManage(house, authentication);
        roomRepository.delete(room);
        house.setTotalRooms(Math.max(0, house.getTotalRooms() - 1));
        houseRepository.save(house);
    }

    @Override
    public void deleteRoomImages(UUID roomId, List<UUID> imageIds, Authentication authentication) {
        Room room = getRoomById(roomId);
        verifyCanManage(room.getHouse(), authentication);
        if (imageIds == null || imageIds.isEmpty()) {
            throw new IllegalArgumentException("At least one room image ID is required");
        }
        List<UUID> roomImageIds = room.getImages().stream().map(RoomImage::getId).toList();
        if (!roomImageIds.containsAll(imageIds)) {
            throw new EntityNotFoundException("One or more image IDs do not belong to this room");
        }
        room.getImages().removeIf(image -> imageIds.contains(image.getId()));
        roomRepository.save(room);
    }

    private House getHouse(UUID houseId) {
        return houseRepository.findById(houseId)
                .orElseThrow(() -> new EntityNotFoundException("House not found"));
    }

    private User resolveClient(UUID clientId) {
        return clientId == null ? null
                : userRepository.findById(clientId)
                        .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    private void verifyCanManage(House house, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new AccessDeniedException("Authentication is required");
        }
        boolean adminOrAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_AGENT"));
        if (adminOrAgent)
            return;

        boolean host = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_HOST"));
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
        if (!host || !house.getHostId().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only manage rooms for your own houses");
        }
    }

    private void addImages(Room room, List<HouseImageRequest> images) {
        if (images == null)
            return;
        for (HouseImageRequest request : images) {
            if (request.getBase64Data() == null || request.getBase64Data().isBlank()) {
                throw new IllegalArgumentException("Room image data is required");
            }
            try {
                RoomImage image = new RoomImage();
                image.setImageUrl(imageUtils.saveRoomImage(request.getBase64Data(), room.getRoomId()));
                image.setCategory(request.getCategory());
                image.setRoom(room);
                room.getImages().add(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save room image", e);
            }
        }
    }
}
