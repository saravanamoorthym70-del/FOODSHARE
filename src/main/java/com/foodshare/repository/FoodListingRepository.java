package com.foodshare.repository;

import com.foodshare.entity.FoodListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodListingRepository extends JpaRepository<FoodListing, Long> {

    List<FoodListing> findByStatus(String status);

    List<FoodListing> findByDonorId(Long donorId);

    List<FoodListing> findByStatusAndSafeToEatUntilBefore(String status, LocalDateTime dateTime);

    @Query("SELECT fl FROM FoodListing fl WHERE fl.status = 'AVAILABLE' AND fl.safeToEatUntil > :now")
    List<FoodListing> findAvailableListings(@Param("now") LocalDateTime now);

    @Query("SELECT fl FROM FoodListing fl WHERE fl.status = 'COLLECTED' AND fl.updatedAt BETWEEN :start AND :end")
    List<FoodListing> findCollectedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
