package com.foodshare.service;

import com.foodshare.entity.Donor;
import com.foodshare.entity.FoodListing;
import com.foodshare.exception.InvalidOperationException;
import com.foodshare.exception.ResourceNotFoundException;
import com.foodshare.repository.FoodListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoodListingService {

    @Autowired
    private FoodListingRepository foodListingRepository;

    @Autowired
    private DonorService donorService;

    public FoodListing createFoodListing(Long donorId, FoodListing foodListing) {
        Donor donor = donorService.getDonorById(donorId);

        // Validate safe-to-eat-until is in the future
        if (foodListing.getSafeToEatUntil().isBefore(LocalDateTime.now())) {
            throw new InvalidOperationException("Safe-to-eat-until time must be in the future.");
        }

        foodListing.setDonor(donor);
        foodListing.setStatus("AVAILABLE");
        return foodListingRepository.save(foodListing);
    }

    public List<FoodListing> getAllFoodListings() {
        return foodListingRepository.findAll();
    }

    public FoodListing getFoodListingById(Long id) {
        return foodListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food listing not found with id: " + id));
    }

    public List<FoodListing> getAvailableListings() {
        return foodListingRepository.findAvailableListings(LocalDateTime.now());
    }

    public List<FoodListing> getListingsByDonor(Long donorId) {
        // Verify donor exists
        donorService.getDonorById(donorId);
        return foodListingRepository.findByDonorId(donorId);
    }

    public FoodListing updateFoodListing(Long id, FoodListing listingDetails) {
        FoodListing listing = getFoodListingById(id);

        if (!"AVAILABLE".equals(listing.getStatus())) {
            throw new InvalidOperationException("Only AVAILABLE listings can be updated.");
        }

        listing.setFoodName(listingDetails.getFoodName());
        listing.setFoodType(listingDetails.getFoodType());
        listing.setQuantity(listingDetails.getQuantity());
        listing.setUnit(listingDetails.getUnit());
        listing.setDescription(listingDetails.getDescription());
        listing.setSafeToEatUntil(listingDetails.getSafeToEatUntil());
        return foodListingRepository.save(listing);
    }

    public void deleteFoodListing(Long id) {
        FoodListing listing = getFoodListingById(id);
        foodListingRepository.delete(listing);
    }

    /**
     * Auto-expire all AVAILABLE listings whose safe-to-eat-until time has passed.
     * Called by the scheduler.
     */
    public int expireListings() {
        List<FoodListing> expiredListings = foodListingRepository
                .findByStatusAndSafeToEatUntilBefore("AVAILABLE", LocalDateTime.now());

        for (FoodListing listing : expiredListings) {
            listing.setStatus("EXPIRED");
            foodListingRepository.save(listing);
        }

        return expiredListings.size();
    }

    /**
     * Get collected listings between two dates (for monthly reports).
     */
    public List<FoodListing> getCollectedListingsBetween(LocalDateTime start, LocalDateTime end) {
        return foodListingRepository.findCollectedBetween(start, end);
    }
}
