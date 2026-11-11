package com.wssoftwares.ikutibnb_backend.house;

import com.wssoftwares.ikutibnb_backend.house.dto.CreateRoom;
import com.wssoftwares.ikutibnb_backend.house.dto.UpdateRoom;
import com.wssoftwares.ikutibnb_backend.house.entity.Room;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    Room createRoom(UUID houseId, CreateRoom dto, Authentication authentication);

    List<Room> getRoomsByHouseId(UUID houseId);

    Room getRoomById(UUID roomId);

    Room updateRoom(UUID roomId, UpdateRoom dto, Authentication authentication);

    void deleteRoom(UUID roomId, Authentication authentication);

    void deleteRoomImages(UUID roomId, List<UUID> imageIds, Authentication authentication);
}
