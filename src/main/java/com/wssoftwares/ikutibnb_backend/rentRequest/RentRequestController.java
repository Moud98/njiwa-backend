package com.wssoftwares.ikutibnb_backend.rentRequest;

import com.wssoftwares.ikutibnb_backend.rentRequest.dto.CreateRentRequestDto;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.UpdateRentRequestStatusDto;
import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.RentRequestResponse;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.AddMonthsDto;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.UpdateTimerDto;
import com.wssoftwares.ikutibnb_backend.house.dto.RoomResponse;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.ApprovedRoomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/rent")
public class RentRequestController {

    private final RentRequestService rentRequestService;

    public RentRequestController(RentRequestService rentRequestService) {
        this.rentRequestService = rentRequestService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('HOST') or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateRentRequestDto dto,
            Authentication authentication) {
        try {
            RentRequest created = rentRequestService.createRequest(dto, authentication);
            return ResponseEntity.ok(new RentRequestResponse(created));
        } catch (IllegalStateException e) {
            if ("property already booked".equals(e.getMessage())) {
                var body = java.util.Map.of("error", e.getMessage());
                return ResponseEntity.status(401).body(body);
            }
            throw e;
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENT') or hasRole('HOST') or hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<Map<String, Object>> getRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        Page<RentRequest> requestsPage = rentRequestService.getRequestsForAuthenticated(page, size, authentication);

        List<RentRequestResponse> resp = requestsPage.getContent()
                .stream()
                .map(RentRequestResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("requests", resp);
        response.put("totalPages", requestsPage.getTotalPages());
        response.put("totalElements", requestsPage.getTotalElements());
        response.put("currentPage", requestsPage.getNumber() + 1);
        response.put("pageSize", requestsPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved")
    @PreAuthorize("hasAnyRole('CLIENT','HOST','ADMIN','AGENT')")
    public ResponseEntity<Map<String, Object>> getApprovedRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String houseName,
            @RequestParam(required = false) String fullname,
            Authentication authentication) {

        Page<RentRequest> requestsPage = rentRequestService.getApprovedRequestsForAuthenticated(
                authentication,
                houseName,
                fullname,
                page,
                size);

        List<ApprovedRoomResponse> resp = requestsPage.getContent()
                .stream()
                .filter(r -> r.getRoom() != null)
                .map(r -> new ApprovedRoomResponse(
                        r.getId(),
                        new RoomResponse(r.getRoom()),
                        r.getDaysRemaining(),
                        r.getEndDate()))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("approvedRooms", resp);
        response.put("totalPages", requestsPage.getTotalPages());
        response.put("totalElements", requestsPage.getTotalElements());
        response.put("currentPage", requestsPage.getNumber() + 1);
        response.put("pageSize", requestsPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentRequestResponse> getById(@PathVariable UUID id, Authentication authentication) {
        RentRequest r = rentRequestService.getById(id, authentication);
        return ResponseEntity.ok(new RentRequestResponse(r));
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')")
    public ResponseEntity<RentRequestResponse> updateStatus(@PathVariable UUID id,
            @Valid @RequestBody UpdateRentRequestStatusDto dto,
            Authentication authentication) {

        RentRequest updated = rentRequestService.updateRequestStatus(id, dto, authentication);
        return ResponseEntity.ok(new RentRequestResponse(updated));
    }

    @PostMapping("/timer-add/{id}")
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public ResponseEntity<RentRequestResponse> addMonthsToTimer(@PathVariable UUID id,
            @Valid @RequestBody AddMonthsDto dto,
            Authentication authentication) {

        RentRequest updated = rentRequestService.addMonthsToTimer(id, dto.getMonths(), authentication);
        return ResponseEntity.ok(new RentRequestResponse(updated));
    }

    @PatchMapping("/timer/{id}")
    @PreAuthorize("hasAnyRole('HOST','ADMIN')")
    public ResponseEntity<RentRequestResponse> updateTimer(@PathVariable UUID id,
            @Valid @RequestBody UpdateTimerDto dto,
            Authentication authentication) {

        RentRequest updated = rentRequestService.updateTimer(id, dto.getDays(), dto.getEndDate(), authentication);
        return ResponseEntity.ok(new RentRequestResponse(updated));
    }
}
