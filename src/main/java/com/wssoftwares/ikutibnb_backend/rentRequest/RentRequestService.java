package com.wssoftwares.ikutibnb_backend.rentRequest;

import com.wssoftwares.ikutibnb_backend.house.entity.Room;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.CreateRentRequestDto;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.UpdateRentRequestStatusDto;
import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import java.util.UUID;


public interface RentRequestService {
    RentRequest createRequest(CreateRentRequestDto dto, Authentication authentication);

    Page<RentRequest> getRequestsForAuthenticated(int page, int size, Authentication authentication);

    Page<Room> getApprovedRoomsForAuthenticated(
            Authentication authentication,
            int page,
            int size);

    // Return approved rent requests (keeps approval details including timer)
    Page<RentRequest> getApprovedRequestsForAuthenticated(
            Authentication authentication,
            String houseName,
            String fullname,
            int page,
            int size);

        RentRequest updateRequestStatus(UUID requestId, UpdateRentRequestStatusDto dto, Authentication authentication);

        RentRequest getById(UUID requestId, Authentication authentication);

        RentRequest addMonthsToTimer(UUID requestId, Integer months, Authentication authentication);

        RentRequest updateTimer(UUID requestId, Long days, String endDate, Authentication authentication);
}
