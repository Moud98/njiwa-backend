package com.wssoftwares.ikutibnb_backend.rentRequest;

import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;
import com.wssoftwares.ikutibnb_backend.house.entity.Room;
import com.wssoftwares.ikutibnb_backend.house.entity.RoomStatus;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

public interface RentRequestRepository extends JpaRepository<RentRequest, UUID> {

    // ---------------- Approved requests with optional filters ----------------
    @Query("""
            SELECT r
            FROM RentRequest r
            JOIN FETCH r.room room
            JOIN FETCH room.house h
            WHERE r.client = :client
              AND r.room.status = :status
              AND (:houseName IS NULL OR h.houseName LIKE %:houseName%)
              AND (:fullname IS NULL OR r.client.fullname LIKE %:fullname%)
            """)
    Page<RentRequest> findApprovedRequestsByClient(
            @Param("client") User client,
            @Param("status") RoomStatus status,
            @Param("houseName") String houseName,
            @Param("fullname") String fullname,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM RentRequest r
            JOIN FETCH r.room room
            JOIN FETCH room.house h
            WHERE r.agent = :agent
              AND r.room.status = :status
              AND (:houseName IS NULL OR h.houseName LIKE %:houseName%)
              AND (:fullname IS NULL OR r.client.fullname LIKE %:fullname%)
            """)
    Page<RentRequest> findApprovedRequestsByAgent(
            @Param("agent") User agent,
            @Param("status") RoomStatus status,
            @Param("houseName") String houseName,
            @Param("fullname") String fullname,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM RentRequest r
            JOIN FETCH r.room room
            JOIN FETCH room.house h
            WHERE r.houseOwner = :owner
              AND r.room.status = :status
              AND (:houseName IS NULL OR h.houseName LIKE %:houseName%)
              AND (:fullname IS NULL OR r.client.fullname LIKE %:fullname%)
            """)
    Page<RentRequest> findApprovedRequestsByOwner(
            @Param("owner") User owner,
            @Param("status") RoomStatus status,
            @Param("houseName") String houseName,
            @Param("fullname") String fullname,
            Pageable pageable
    );

    // ---------------- All requests by role with pagination ----------------
    @Query("""
            SELECT r
            FROM RentRequest r
            JOIN FETCH r.room room
            JOIN FETCH room.house h
            WHERE r.client = :client
            """)
    Page<RentRequest> findRequestsByClient(User client, Pageable pageable);

    @Query("""
            SELECT r
            FROM RentRequest r
            JOIN FETCH r.room room
            JOIN FETCH room.house h
            WHERE r.houseOwner = :owner
            """)
    Page<RentRequest> findRequestsByHouseOwner(User owner, Pageable pageable);

    @Query("""
            SELECT r
            FROM RentRequest r
            JOIN FETCH r.room room
            JOIN FETCH room.house h
            WHERE r.agent = :agent
            """)
    Page<RentRequest> findRequestsByAgent(User agent, Pageable pageable);

        List<RentRequest> findByRoom(Room room);

    // -------- Reminder-related queries --------
    /**
     * Find all active approved rent requests with 15 or fewer days remaining
     * that haven't had a reminder sent yet
     */
    @Query("""
            SELECT r
            FROM RentRequest r
            JOIN FETCH r.client
            JOIN FETCH r.houseOwner
            JOIN FETCH r.room room
            JOIN FETCH room.house
            WHERE r.room.status = 'APPROVED'
              AND r.timer <= 15
              AND r.reminderSent = false
            """)
    List<RentRequest> findRentRequestsNeedingReminders();

    /**
     * Find rent requests where the reminder was sent and 24 hours have passed
     * so the reminder flag can be reset
     */
    @Query("""
            SELECT r
            FROM RentRequest r
            WHERE r.reminderSent = true
              AND r.reminderSentTimestamp IS NOT NULL
              AND r.reminderSentTimestamp <= :twentyFourHoursAgo
            """)
    List<RentRequest> findRentRequestsToResetReminder(@Param("twentyFourHoursAgo") LocalDateTime twentyFourHoursAgo);
}
