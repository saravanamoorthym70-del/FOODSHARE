package com.foodshare.service;

import com.foodshare.entity.Claim;
import com.foodshare.entity.FoodListing;
import com.foodshare.entity.NGO;
import com.foodshare.exception.DuplicateClaimException;
import com.foodshare.exception.InvalidOperationException;
import com.foodshare.exception.ListingExpiredException;
import com.foodshare.exception.ResourceNotFoundException;
import com.foodshare.repository.ClaimRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClaimService {

    private static final Logger logger = LoggerFactory.getLogger(ClaimService.class);

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private FoodListingService foodListingService;

    @Autowired
    private NGOService ngoService;

    /**
     * NGO claims an available food listing.
     * Business Rules enforced:
     * 1. Listing must not be past its safe-to-eat-until time.
     * 2. Only one NGO can hold an active claim on a listing at a time.
     */
    @Transactional
    public Claim claimListing(Long foodListingId, Long ngoId) {
        FoodListing listing = foodListingService.getFoodListingById(foodListingId);
        NGO ngo = ngoService.getNGOById(ngoId);

        // BUSINESS RULE 1: A listing past its safe-to-eat-until time can no longer be claimed
        if (listing.getSafeToEatUntil().isBefore(LocalDateTime.now())) {
            throw new ListingExpiredException(
                    "This listing has expired (safe-to-eat-until: " + listing.getSafeToEatUntil()
                            + "). It can no longer be claimed.");
        }

        // Check listing is AVAILABLE
        if (!"AVAILABLE".equals(listing.getStatus())) {
            throw new InvalidOperationException(
                    "This listing is not available for claiming. Current status: " + listing.getStatus());
        }

        // BUSINESS RULE 2: Only one NGO can hold an active claim on a listing at a time
        if (claimRepository.existsByFoodListingIdAndStatus(foodListingId, "ACTIVE")) {
            throw new DuplicateClaimException(
                    "An active claim already exists for this listing. Only one NGO can claim a listing at a time.");
        }

        // Create the claim
        Claim claim = new Claim();
        claim.setNgo(ngo);
        claim.setFoodListing(listing);
        claim.setStatus("ACTIVE");

        // Update listing status to CLAIMED
        listing.setStatus("CLAIMED");

        Claim savedClaim = claimRepository.save(claim);
        logger.info("NGO '{}' claimed listing '{}' (ID: {})", ngo.getName(), listing.getFoodName(), listing.getId());

        return savedClaim;
    }

    /**
     * Mark a listing as collected once picked up.
     */
    @Transactional
    public Claim markAsCollected(Long claimId) {
        Claim claim = getClaimById(claimId);

        if (!"ACTIVE".equals(claim.getStatus())) {
            throw new InvalidOperationException(
                    "Only active claims can be marked as collected. Current status: " + claim.getStatus());
        }

        claim.setStatus("COLLECTED");
        claim.setCollectedAt(LocalDateTime.now());

        // Update listing status to COLLECTED
        FoodListing listing = claim.getFoodListing();
        listing.setStatus("COLLECTED");

        Claim savedClaim = claimRepository.save(claim);
        logger.info("Claim {} marked as COLLECTED. Listing '{}' picked up by NGO '{}'.",
                claimId, listing.getFoodName(), claim.getNgo().getName());

        return savedClaim;
    }

    /**
     * Cancel an active claim, making the listing available again.
     */
    @Transactional
    public Claim cancelClaim(Long claimId) {
        Claim claim = getClaimById(claimId);

        if (!"ACTIVE".equals(claim.getStatus())) {
            throw new InvalidOperationException(
                    "Only active claims can be cancelled. Current status: " + claim.getStatus());
        }

        claim.setStatus("CANCELLED");

        // Make listing available again
        FoodListing listing = claim.getFoodListing();
        listing.setStatus("AVAILABLE");

        Claim savedClaim = claimRepository.save(claim);
        logger.info("Claim {} cancelled. Listing '{}' is now available again.", claimId, listing.getFoodName());

        return savedClaim;
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<Claim> getClaimsByNGO(Long ngoId) {
        // Verify NGO exists
        ngoService.getNGOById(ngoId);
        return claimRepository.findByNgoId(ngoId);
    }
}
