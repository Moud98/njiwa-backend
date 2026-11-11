package com.wssoftwares.ikutibnb_backend.house.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "room_image")
public class RoomImage {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String imageUrl;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
}
