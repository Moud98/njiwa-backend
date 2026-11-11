package com.wssoftwares.ikutibnb_backend.house;

import com.wssoftwares.ikutibnb_backend.house.dto.CreateRoom;
import com.wssoftwares.ikutibnb_backend.house.dto.RoomResponse;
import com.wssoftwares.ikutibnb_backend.house.dto.UpdateRoom;
import com.wssoftwares.ikutibnb_backend.house.entity.Room;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/house")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/rooms/{houseId:[0-9a-fA-F]+-[0-9a-fA-F-]+}")
    @PreAuthorize("hasAnyRole('HOST', 'AGENT', 'ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(@PathVariable UUID houseId, @Valid @RequestBody CreateRoom dto,
            Authentication authentication) {
        return ResponseEntity.ok(new RoomResponse(roomService.createRoom(houseId, dto, authentication)));
    }

    @GetMapping("/rooms/{houseId:[0-9a-fA-F]+-[0-9a-fA-F-]+}")
    public ResponseEntity<List<RoomResponse>> getRoomsByHouseId(@PathVariable UUID houseId) {
        return ResponseEntity.ok(roomService.getRoomsByHouseId(houseId).stream().map(RoomResponse::new).toList());
    }

    @GetMapping("/rooms/{roomId:[0-9a-fA-F]+-[0-9a-fA-F-]+}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable UUID roomId) {
        return ResponseEntity.ok(new RoomResponse(roomService.getRoomById(roomId)));
    }

    @PatchMapping("/rooms/{roomId:[0-9a-fA-F]+-[0-9a-fA-F-]+}")
    @PreAuthorize("hasAnyRole('HOST', 'AGENT', 'ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable UUID roomId, @RequestBody UpdateRoom dto,
            Authentication authentication) {
        return ResponseEntity.ok(new RoomResponse(roomService.updateRoom(roomId, dto, authentication)));
    }

    @DeleteMapping("/rooms/images/{roomId:[0-9a-fA-F]+-[0-9a-fA-F-]+}")
    @PreAuthorize("hasAnyRole('HOST', 'AGENT', 'ADMIN')")
    public ResponseEntity<Void> deleteRoomImages(@PathVariable UUID roomId, @RequestBody List<UUID> imageIds,
            Authentication authentication) {
        roomService.deleteRoomImages(roomId, imageIds, authentication);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rooms/{roomId:[0-9a-fA-F]+-[0-9a-fA-F-]+}")
    @PreAuthorize("hasAnyRole('HOST', 'AGENT', 'ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId, Authentication authentication) {
        roomService.deleteRoom(roomId, authentication);
        return ResponseEntity.noContent().build();
    }
}
