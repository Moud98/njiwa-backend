package com.wssoftwares.ikutibnb_backend.rentRequest.service;

import com.wssoftwares.ikutibnb_backend.rentRequest.RentRequestRepository;
import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;
import com.wssoftwares.ikutibnb_backend.house.entity.RoomStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RentRequestTimerScheduler {

    private final RentRequestRepository rentRequestRepository;

    public RentRequestTimerScheduler(RentRequestRepository rentRequestRepository) {
        this.rentRequestRepository = rentRequestRepository;
    }

    /**
     * Decrements the timer by 1 day for all approved rent requests at midnight
     * Scheduled to run every day at 00:00 (midnight)
     */
    @Scheduled(cron = "0 0 0 * * *")
    // @Scheduled(cron = "0 */1 * * * *") // For testing purposes, runs every minute       
    @Transactional
    public void decrementTimerAtMidnight() {
        // Find all approved rent requests with active timers
        List<RentRequest> approvedRequests = rentRequestRepository
                .findAll()
                .stream()
                .filter(rr -> rr.getRoom().getStatus() == RoomStatus.APPROVED && rr.getTimer() != null && rr.getTimer() > 0)
                .toList();

        for (RentRequest request : approvedRequests) {
            // Decrement timer by 1 day
            long newTimer = request.getTimer() - 1;
            request.setTimer(Math.max(0, newTimer)); // Ensure it doesn't go below 0
            rentRequestRepository.save(request);
        }
    }
}
