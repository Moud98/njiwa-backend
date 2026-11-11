package com.wssoftwares.ikutibnb_backend.rentRequest.impl;

import com.wssoftwares.ikutibnb_backend.rentRequest.RentRequestService;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.CreateRentRequestDto;
import com.wssoftwares.ikutibnb_backend.rentRequest.dto.UpdateRentRequestStatusDto;
import com.wssoftwares.ikutibnb_backend.rentRequest.entity.RentRequest;
import com.wssoftwares.ikutibnb_backend.rentRequest.RentRequestRepository;
import com.wssoftwares.ikutibnb_backend.house.RoomRepository;
import com.wssoftwares.ikutibnb_backend.house.entity.House;
import com.wssoftwares.ikutibnb_backend.house.entity.Room;
import com.wssoftwares.ikutibnb_backend.house.entity.RoomStatus;
import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RentRequestServiceImpl implements RentRequestService {

    private final RentRequestRepository rentRequestRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public RentRequestServiceImpl(RentRequestRepository rentRequestRepository,
            UserRepository userRepository,
            RoomRepository roomRepository) {
        this.rentRequestRepository = rentRequestRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    private User getAuthUser(Authentication authentication) {
        UserDetails ud = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(ud.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found"));
    }

    @Override
    public RentRequest createRequest(CreateRentRequestDto dto, Authentication authentication) {
        User authUser = getAuthUser(authentication);

        // ensure clientId matches authenticated user (unless ADMIN)
        if (!authUser.getId().equals(dto.getClientId())) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                throw new AccessDeniedException("Client id does not match authenticated user");
            }
        }

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        House house = room.getHouse();

        // Directly get host and agent from House entity
        User owner = house.getHostId(); // host/owner
        User agent = house.getAgentId(); // registering agent

        // A room can have only one active request; other rooms in its house remain available.
        boolean hasActiveRequest = !rentRequestRepository.findByRoom(room).isEmpty();
        if (hasActiveRequest) {
            throw new IllegalStateException("property already booked");
        }

        RentRequest rr = new RentRequest();
        rr.setClient(authUser);
        rr.setRoom(room);
        rr.setHouseOwner(owner);
        rr.setAgent(agent);

        room.setClientId(authUser);
        room.setClientName(dto.getClientName());
        room.setClientPhone(dto.getClientPhone());
        room.setStatus(RoomStatus.PENDING);
        roomRepository.save(room);

        return rentRequestRepository.save(rr);
    }

    @Override
    public Page<RentRequest> getRequestsForAuthenticated(int page, int size, Authentication authentication) {
        User authUser = getAuthUser(authentication);

        boolean isClient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
        boolean isHost = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HOST"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));

        Pageable pageable = PageRequest.of(page, size);

        if (isClient) {
            return rentRequestRepository.findRequestsByClient(authUser, pageable);
        } else if (isHost) {
            return rentRequestRepository.findRequestsByHouseOwner(authUser, pageable);
        } else if (isAgent) {
            return rentRequestRepository.findRequestsByAgent(authUser, pageable);
        } else {
            throw new AccessDeniedException("User role not allowed to fetch rent requests");
        }
    }

    @Override
    public Page<Room> getApprovedRoomsForAuthenticated(
            Authentication authentication,
            int page,
            int size) {

        User authUser = getAuthUser(authentication);
        Pageable pageable = PageRequest.of(page, size);

        boolean isClient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));

        boolean isHost = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HOST"));

        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));

        Page<RentRequest> rentRequests;

        if (isClient) {

            rentRequests = rentRequestRepository
                    .findApprovedRequestsByClient(
                            authUser,
                            RoomStatus.APPROVED,
                            null,
                            null,
                            pageable);

        } else if (isAgent) {

            rentRequests = rentRequestRepository
                    .findApprovedRequestsByAgent(
                            authUser,
                            RoomStatus.APPROVED,
                            null,
                            null,
                            pageable);

        } else if (isHost) {

            rentRequests = rentRequestRepository
                    .findApprovedRequestsByOwner(
                            authUser,
                            RoomStatus.APPROVED,
                            null,
                            null,
                            pageable);

        } else {
            throw new AccessDeniedException("User role not allowed to fetch approved rooms");
        }

        return rentRequests.map(RentRequest::getRoom);
    }

    @Override
    public Page<RentRequest> getApprovedRequestsForAuthenticated(
            Authentication authentication,
            String houseName,
            String fullname,
            int page,
            int size) {

        User authUser = getAuthUser(authentication);
        Pageable pageable = PageRequest.of(page, size);

        boolean isClient = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"));
        boolean isHost = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HOST"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));

        if (isClient) {

            return rentRequestRepository.findApprovedRequestsByClient(
                    authUser,
                    RoomStatus.APPROVED,
                    houseName,
                    fullname,
                    pageable);

        } else if (isAgent) {

            return rentRequestRepository.findApprovedRequestsByAgent(
                    authUser,
                    RoomStatus.APPROVED,
                    houseName,
                    fullname,
                    pageable);

        } else if (isHost) {

            return rentRequestRepository.findApprovedRequestsByOwner(
                    authUser,
                    RoomStatus.APPROVED,
                    houseName,
                    fullname,
                    pageable);

        } else {
            throw new AccessDeniedException("User role not allowed to fetch approved requests");
        }
    }

    @Override
    public RentRequest updateRequestStatus(UUID requestId, UpdateRentRequestStatusDto dto,
            Authentication authentication) {
        RentRequest request = rentRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rent request not found"));

        User authUser = getAuthUser(authentication);

        boolean isOwner = request.getHouseOwner() != null && authUser.getId().equals(request.getHouseOwner().getId());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not authorized to update this request");
        }

        // determine new status
        RoomStatus newStatus = dto.getStatus() != null ? dto.getStatus() : request.getRoom().getStatus();

        // Handle DECLINED status: delete the rent request and free its room.
        if (newStatus == RoomStatus.DECLINED) {
            Room room = request.getRoom();
            if (room != null) {
                room.setStatus(RoomStatus.AVAILABLE);
                room.setClientId(null);
                room.setClientName(null);
                room.setClientPhone(null);
                roomRepository.save(room);
            }
            // Delete the rent request
            rentRequestRepository.delete(request);
            return request;
        }

        // Set approval timestamp and calculate timer when status changes to APPROVED
        if (newStatus == RoomStatus.APPROVED && request.getRoom().getStatus() != RoomStatus.APPROVED) {
            request.setApprovalTimestamp(LocalDateTime.now());

            // Convert payment duration (months) to days and set timer
            if (request.getRoom() != null && request.getRoom().getPaymentDuration() != null) {
                LocalDate endDate = LocalDate.now().plusMonths(request.getRoom().getPaymentDuration());
                request.setEndDate(endDate.toString());
                request.setTimer(ChronoUnit.DAYS.between(LocalDate.now(), endDate));
            }
        }

        request.getRoom().setStatus(newStatus);
        roomRepository.save(request.getRoom());
        RentRequest savedRequest = rentRequestRepository.save(request);

        // When a HOST/ADMIN approves a request, mark only the selected room as taken.
        if (newStatus == RoomStatus.APPROVED) {
            Room room = savedRequest.getRoom();
            if (room != null) {
                room.setStatus(RoomStatus.APPROVED);
                room.setClientId(savedRequest.getClient());
                roomRepository.save(room);
            }
        }

        return savedRequest;
    }

    @Override
    public RentRequest addMonthsToTimer(UUID requestId, Integer months, Authentication authentication) {
        RentRequest request = rentRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rent request not found"));

        User authUser = getAuthUser(authentication);

        boolean isOwner = request.getHouseOwner() != null && authUser.getId().equals(request.getHouseOwner().getId());
        boolean isClient = request.getClient() != null && authUser.getId().equals(request.getClient().getId());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin && !isClient) {
            throw new AccessDeniedException("Not authorized to modify this request timer");
        }

        if (months == null || months <= 0) {
            return request;
        }

        LocalDate baseDate = request.getEndDate() != null
                ? LocalDate.parse(request.getEndDate()) : LocalDate.now();
        LocalDate endDate = baseDate.plusMonths(months);
        request.setEndDate(endDate.toString());
        request.setTimer(Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), endDate)));

        return rentRequestRepository.save(request);
    }

    @Override
    public RentRequest updateTimer(UUID requestId,
            Long days,
            String endDate,
            Authentication authentication) {

        RentRequest request = rentRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rent request not found"));

        User authUser = getAuthUser(authentication);

        boolean isOwner = request.getHouseOwner() != null &&
                authUser.getId().equals(request.getHouseOwner().getId());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not authorized to modify this request timer");
        }

        request.setTimer(days != null ? days : 0L);
        if (endDate != null && !endDate.isBlank()) {
            LocalDate parsedEndDate = LocalDate.parse(endDate);
            request.setEndDate(parsedEndDate.toString());
            request.setTimer(Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), parsedEndDate)));
        } else {
            request.setEndDate(null);
        }

        return rentRequestRepository.save(request);
    }

    @Override
    public RentRequest getById(UUID requestId, Authentication authentication) {
        RentRequest request = rentRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Rent request not found"));

        User authUser = getAuthUser(authentication);
        boolean isClient = request.getClient() != null && authUser.getId().equals(request.getClient().getId());
        boolean isOwner = request.getHouseOwner() != null && authUser.getId().equals(request.getHouseOwner().getId());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isClient && !isOwner && !isAdmin) {
            throw new AccessDeniedException("Not authorized to view this request");
        }

        return request;
    }
}
