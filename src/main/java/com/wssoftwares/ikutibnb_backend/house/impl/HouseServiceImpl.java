package com.wssoftwares.ikutibnb_backend.house.impl;

import com.wssoftwares.ikutibnb_backend.house.dto.CreateHouse;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseDTO;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseMapper;
import com.wssoftwares.ikutibnb_backend.house.dto.UpdateHouse;
import com.wssoftwares.ikutibnb_backend.house.dto.HouseImageRequest;
import com.wssoftwares.ikutibnb_backend.house.entity.House;
import com.wssoftwares.ikutibnb_backend.house.entity.HouseImage;
import com.wssoftwares.ikutibnb_backend.house.entity.HouseState;
import com.wssoftwares.ikutibnb_backend.house.entity.HouseType;
import com.wssoftwares.ikutibnb_backend.house.HouseRepository;
import com.wssoftwares.ikutibnb_backend.house.HouseService;
import com.wssoftwares.ikutibnb_backend.house.HouseImageRepository;
import com.wssoftwares.ikutibnb_backend.house.entity.Room;
import com.wssoftwares.ikutibnb_backend.house.entity.RoomStatus;
import com.wssoftwares.ikutibnb_backend.house.ImageUtils;
import com.wssoftwares.ikutibnb_backend.users.UserRepository;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import com.wssoftwares.ikutibnb_backend.users.entity.Role;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class HouseServiceImpl implements HouseService {

    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final HouseImageRepository houseImageRepository;
    private final ImageUtils imageUtils;

    public HouseServiceImpl(
            HouseRepository houseRepository,
            UserRepository userRepository,
            HouseImageRepository houseImageRepository,
            ImageUtils imageUtils) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.houseImageRepository = houseImageRepository;
        this.imageUtils = imageUtils;
    }

    @Override
    public House CreateHouse(CreateHouse dto, Authentication authentication) {
        Logger log = LoggerFactory.getLogger(this.getClass());

        List<HouseImageRequest> images = dto.getImages();
        if (images != null) {
            for (HouseImageRequest image : images) {
                if (image == null || image.getBase64Data() == null || image.getBase64Data().isBlank()) {
                    throw new IllegalArgumentException("Each image must include base64Data");
                }
            }
            for (int i = 1; i < images.size(); i++) {
                if (images.get(i).getCategory() == null || images.get(i).getCategory().isBlank()) {
                    throw new IllegalArgumentException("Each additional image must include a category");
                }
            }
        }

        User host = userRepository.findById(dto.getHostId())
                .orElseThrow(() -> new EntityNotFoundException("Host not found"));

        User agent = dto.getAgentId() == null ? null : userRepository.findById(dto.getAgentId())
            .orElseThrow(() -> new EntityNotFoundException("Agent not found"));

        if (host.getRole() != Role.HOST
            && (agent == null || (agent.getRole() != Role.AGENT && agent.getRole() != Role.ADMIN))) {
            throw new RuntimeException("User is not an agent or admin");
        }

        // create house WITHOUT element collections
        House house = new House();
        house.setLocation(dto.getLocation());
        house.setHouseNumber(dto.getHouseNumber());
        house.setHouseName(dto.getHouseName());
        house.setPaymentDuration(dto.getPaymentDuration());
        house.setHouseDescription(dto.getHouseDescription());
        house.setRating(dto.getRating());
        house.setReviews(dto.getReviews());
        house.setHostId(host);
        house.setAgentId(agent);

        house = houseRepository.save(house);

        if (dto.getHouseServices() != null) {
            house.setHouseServices(new HashSet<>(dto.getHouseServices()));
        }
        if (dto.getCoordinates() != null) {
            house.setCoordinates(new ArrayList<>(dto.getCoordinates()));
        }
        if (dto.getPaymentMethods() != null) {
            house.setPaymentMethods(new HashSet<>(dto.getPaymentMethods()));
        }
        if (dto.getDocuments() != null) {
            house.setDocuments(new HashSet<>(dto.getDocuments()));
        }

        house = houseRepository.save(house);

        // image handling
        String primaryImage = images != null && !images.isEmpty()
                ? images.get(0).getBase64Data()
                : dto.getImageUrl();
        if (primaryImage != null && !primaryImage.isEmpty()) {
            try {
                log.info("Saving image for houseId={}", house.getHouseId());

                String storedUrl = imageUtils.saveHouseImage(
                        primaryImage,
                        house.getHouseId());

                house.setImageUrl(storedUrl);
                house = houseRepository.save(house);

            } catch (IOException e) {
                log.error("Image save failed for houseId={}", house.getHouseId(), e);
                throw new RuntimeException("Failed to save image: " + e.getMessage(), e);

            } catch (IllegalArgumentException e) {
                throw e;

            } catch (Exception e) {
                log.error("Unexpected error while saving image for houseId={}", house.getHouseId(), e);
                throw new RuntimeException("Unexpected error while saving image: " + e.getMessage(), e);
            }
        }

        if (images != null && images.size() > 1) {
            for (int i = 1; i < images.size(); i++) {
                saveHouseImage(house, images.get(i));
            }
            house = houseRepository.save(house);
        }

        return house;
    }

    @Override
    public Page<House> getAllHouses(
            int page,
            int size,
            String location,
            String price,
            HouseType houseType,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<House> spec = Specification.where(null);

        // Rent requests are now attached to rooms, not houses. A room booking must
        // therefore not exclude its whole house from this listing.
        spec = spec.and((root, query, cb) -> cb.equal(root.get("state"), HouseState.AVAILABLE));
        spec = spec.and((root, query, cb) -> {
            Subquery<Room> roomSubquery = query.subquery(Room.class);
            Root<Room> roomRoot = roomSubquery.from(Room.class);
            roomSubquery.select(roomRoot)
                .where(cb.equal(roomRoot.get("house"), root),
                       cb.notEqual(roomRoot.get("status"), RoomStatus.APPROVED));
            return cb.exists(roomSubquery);
        });

        // Case-insensitive location filter
        if (location != null && !location.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(
                    cb.lower(root.get("location")),
                    location.trim().toLowerCase()));
        }

        // Price and house type belong to rooms. Match houses that have a room
        // satisfying the supplied room filters (and keep both filters on the
        // same room when both are supplied).
        if (price != null && !price.trim().isEmpty()) {
            BigDecimal min = null;
            BigDecimal max = null;
            if (price.contains("-")) {
                String[] range = price.split("-");
                if (range.length > 0 && !range[0].isBlank()) {
                    min = new BigDecimal(range[0].trim());
                }

                if (range.length > 1 && !range[1].isBlank()) {
                    max = new BigDecimal(range[1].trim());
                }

            } else {
                min = new BigDecimal(price.trim());
                max = min;
            }

            BigDecimal finalMin = min;
            BigDecimal finalMax = max;
            spec = spec.and((root, query, cb) -> {
                Subquery<Room> roomSubquery = query.subquery(Room.class);
                Root<Room> roomRoot = roomSubquery.from(Room.class);
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(roomRoot.get("house"), root));
                if (finalMin != null && finalMax != null) {
                    predicates.add(cb.between(roomRoot.get("price"), finalMin, finalMax));
                } else if (finalMin != null) {
                    predicates.add(cb.greaterThanOrEqualTo(roomRoot.get("price"), finalMin));
                } else if (finalMax != null) {
                    predicates.add(cb.lessThanOrEqualTo(roomRoot.get("price"), finalMax));
                }
                if (houseType != null) {
                    predicates.add(cb.equal(roomRoot.get("houseType"), houseType));
                }
                roomSubquery.select(roomRoot).where(predicates.toArray(new Predicate[0]));
                return cb.exists(roomSubquery);
            });
        } else if (houseType != null) {
            spec = spec.and((root, query, cb) -> {
                Subquery<Room> roomSubquery = query.subquery(Room.class);
                Root<Room> roomRoot = roomSubquery.from(Room.class);
                roomSubquery.select(roomRoot).where(
                    cb.equal(roomRoot.get("house"), root),
                    cb.equal(roomRoot.get("houseType"), houseType));
                return cb.exists(roomSubquery);
            });
        }

        return houseRepository.findAll(spec, pageable);
    }

    @Override
    public Page<House> getHousesByHost(
            int page,
            int size,
            String houseName,
            String fullname,
            String location,
            Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Specification<House> spec = Specification.where(null);

        // Restrict to houses owned/registered by the current user
        if (user.getRole() == Role.HOST) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("hostId"), user));
        } else if (user.getRole() == Role.AGENT) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("agentId"), user));
        } else if (user.getRole() == Role.ADMIN) {
            spec = spec.and((root, query, cb) -> cb.conjunction());
        } else {
            throw new AccessDeniedException("Only HOST, AGENT or ADMIN can view registered houses");
        }

        // Optional search by house name (case-insensitive, contains)
        if (houseName != null && !houseName.trim().isEmpty()) {
            final String namePattern = "%" + houseName.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("houseName")), namePattern));
        }

        // Optional search by fullname (case-insensitive, contains) for both host and client
        if (fullname != null && !fullname.trim().isEmpty()) {
            final String fullNamePattern = "%" + fullname.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> {
                Predicate hostPredicate = cb.like(cb.lower(root.join("hostId").get("fullname")), fullNamePattern);
                Predicate clientPredicate = cb.like(
                        cb.lower(root.join("rooms", JoinType.LEFT).join("clientId", JoinType.LEFT).get("fullname")),
                        fullNamePattern);
                query.distinct(true);
                return cb.or(hostPredicate, clientPredicate);
            });
        }

        // Optional search by location (case-insensitive, contains)
        if (location != null && !location.trim().isEmpty()) {
            final String locationPattern = "%" + location.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("location")), locationPattern));
        }

        return houseRepository.findAll(spec, pageable);
    }

    @Override
    public House getHouseById(UUID houseId) {
        return houseRepository.findById(houseId)
                .orElseThrow(() -> new EntityNotFoundException("House not found"));
    }

    public List<HouseImage> getImagesByCategory(UUID houseId, String category) {
        return houseImageRepository.findByHouseHouseIdAndCategory(houseId, category);
    }

    @Override
    public House updateHouse(UUID houseId, UpdateHouse dto, Authentication authentication) {

        House house = getHouseById(houseId);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        if ("ROLE_HOST".equals(role)) {
            String email = userDetails.getUsername();
            User host = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Host not found"));

            if (!house.getHostId().getId().equals(host.getId())) {
                throw new AccessDeniedException("You can only update houses you posted.");
            }
        }

        if (dto.getLocation() != null)
            house.setLocation(dto.getLocation());
        if (dto.getHouseNumber() != null)
            house.setHouseNumber(dto.getHouseNumber());
        if (dto.getHouseName() != null)
            house.setHouseName(dto.getHouseName());
        if (dto.getPaymentDuration() != null) {
            house.setPaymentDuration(dto.getPaymentDuration());
            house.getRooms().forEach(room -> room.setPaymentDuration(dto.getPaymentDuration()));
        }
        if (dto.getHouseDescription() != null)
            house.setHouseDescription(dto.getHouseDescription());
        if (dto.getHouseServices() != null)
            house.setHouseServices(dto.getHouseServices());
        if (dto.getCoordinates() != null)
            house.setCoordinates(new ArrayList<>(dto.getCoordinates()));
        if (dto.getPaymentMethods() != null)
            house.setPaymentMethods(dto.getPaymentMethods());
        if (dto.getDocuments() != null)
            house.setDocuments(dto.getDocuments());
        if (dto.getRating() != null)
            house.setRating(dto.getRating());
        if (dto.getReviews() != null)
            house.setReviews(dto.getReviews());

        // Handle imageUrl
        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            try {
                if (isBase64(dto.getImageUrl())) {
                    // Base64 handling
                    String storedUrl = imageUtils.saveHouseImage(dto.getImageUrl(), house.getHouseId());
                    house.setImageUrl(storedUrl);
                } else {
                    // Normal URL handling
                    house.setImageUrl(dto.getImageUrl());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image", e);
            }
        }
        return houseRepository.save(house);
    }

    // Utility method to check if a string looks like Base64.
    private boolean isBase64(String value) {
        // Common base64 strings often start with "data:image"
        if (value.startsWith("data:image")) {
            return true;
        }
        // Try decoding to verify
        try {
            Base64.getDecoder().decode(value.replaceAll("\\s", ""));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void deleteHouse(UUID houseId, Authentication authentication) {

        House house = getHouseById(houseId);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        if ("ROLE_ADMIN".equals(role)) {
            houseRepository.delete(house);
            return;
        }

        if ("ROLE_HOST".equals(role)) {
            String email = userDetails.getUsername();
            User host = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Host not found"));

            if (!house.getHostId().getId().equals(host.getId())) {
                throw new AccessDeniedException("You can only delete houses you posted.");
            }

            houseRepository.delete(house);
            return;
        }

        throw new AccessDeniedException("Unauthorized role for deletion.");
    }

    @Override
    public void addImagesToHouse(
            UUID houseId,
            List<HouseImageRequest> images,
            Authentication authentication) {

        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("Images list cannot be empty");
        }

        House house = getHouseById(houseId);

        for (HouseImageRequest imageRequest : images) {
            if (imageRequest == null || imageRequest.getBase64Data() == null || imageRequest.getCategory() == null) {
                throw new IllegalArgumentException("Invalid image payload");
            }
            saveHouseImage(house, imageRequest);
        }

        houseRepository.save(house);
    }

    private void saveHouseImage(House house, HouseImageRequest imageRequest) {
        try {
            String imageUrl = imageUtils.saveHouseImage(imageRequest.getBase64Data(), house.getHouseId());
            HouseImage image = new HouseImage();
            image.setImageUrl(imageUrl);
            image.setCategory(imageRequest.getCategory());
            image.setHouse(house);
            house.getImages().add(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    @Override
    public void deleteHouseImages(List<UUID> imageIds, Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        User host = null;
        if ("ROLE_HOST".equals(role)) {
            String email = userDetails.getUsername();
            host = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Host not found"));
        }

        for (UUID imageId : imageIds) {
            HouseImage image = houseImageRepository.findById(imageId)
                    .orElseThrow(() -> new EntityNotFoundException("Image not found: " + imageId));

            House house = image.getHouse();

            if ("ROLE_HOST".equals(role)) {
                if (!house.getHostId().getId().equals(host.getId())) {
                    throw new AccessDeniedException("You can only delete images from your own houses.");
                }
            }

            String imagePath = image.getImageUrl().replaceFirst("/uploads/", "uploads/");
            try {
                Files.deleteIfExists(Paths.get(imagePath));
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image file: " + imagePath, e);
            }

            house.getImages().remove(image);
            houseImageRepository.delete(image);
        }
    }

    @Override
    public List<HouseDTO> searchHouses(String location, String price,
            HouseType houseType, Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        Specification<House> spec = Specification.where(null);

        if (location != null && !location.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("location"), location));
        }

        if (price != null && !price.trim().isEmpty()) {
            if (price.contains("-")) {
                String[] range = price.split("-");
                BigDecimal min = new BigDecimal(range[0].trim());
                BigDecimal max = new BigDecimal(range[1].trim());
                spec = spec.and((root, query, cb) -> cb.between(root.get("price"), min, max));
            } else {
                BigDecimal exact = new BigDecimal(price.trim());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("price"), exact));
            }
        }

        if (houseType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("houseType"), houseType));
        }

        if ("ROLE_HOST".equals(role)) {
            String email = userDetails.getUsername();
            User host = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Host not found"));

            spec = spec.and((root, query, cb) -> cb.equal(root.get("hostId"), host));
        }

        return houseRepository.findAll(spec)
                .stream()
                .map(HouseMapper::toDTO)
                .collect(Collectors.toList());
    }

}
