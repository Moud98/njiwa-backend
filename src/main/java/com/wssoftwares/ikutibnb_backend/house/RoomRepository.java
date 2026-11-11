package com.wssoftwares.ikutibnb_backend.house;

import com.wssoftwares.ikutibnb_backend.house.entity.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    @EntityGraph(attributePaths = { "images", "clientId", "house" })
    List<Room> findByHouseHouseIdOrderByRoomIdAsc(UUID houseId);

    @Override
    @EntityGraph(attributePaths = { "images", "clientId", "house" })
    Optional<Room> findById(UUID roomId);
}
