package com.wssoftwares.ikutibnb_backend.house;

import com.wssoftwares.ikutibnb_backend.house.entity.House;
import com.wssoftwares.ikutibnb_backend.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseRepository
                extends JpaRepository<House, UUID>, JpaSpecificationExecutor<House> {

        // REMOVE @EntityGraph from this paginated query
        Page<House> findByHostId(User hostId, Pageable pageable);

        Page<House> findByAgentId(User agentId, Pageable pageable);
        
        // ADD this new method to fetch images separately when needed
        @EntityGraph(attributePaths = {"images", "hostId"})
        @Query("SELECT h FROM House h WHERE h.id IN :ids")
        List<House> findByIdInWithImages(@Param("ids") List<UUID> ids);

        @EntityGraph(attributePaths = {
                        "images",
                        "houseServices",
                        "coordinates",
                        "paymentMethods",
                        "documents",
                        "hostId"
        })
        Optional<House> findById(UUID id);
}