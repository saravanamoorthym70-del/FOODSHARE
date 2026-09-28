package com.foodshare.repository;

import com.foodshare.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByNgoId(Long ngoId);

    Optional<Claim> findByFoodListingIdAndStatus(Long foodListingId, String status);

    boolean existsByFoodListingIdAndStatus(Long foodListingId, String status);

    List<Claim> findByStatus(String status);
}
