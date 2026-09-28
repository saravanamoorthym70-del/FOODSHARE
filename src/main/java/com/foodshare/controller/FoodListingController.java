package com.foodshare.controller;

import com.foodshare.entity.FoodListing;
import com.foodshare.service.FoodListingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-listings")
public class FoodListingController {

    @Autowired
    private FoodListingService foodListingService;

    /**
     * Feature 1: Donor lists surplus food with quantity, type, and safe-to-eat-until time.
     */
    @PostMapping("/donor/{donorId}")
    public ResponseEntity<FoodListing> createFoodListing(
            @PathVariable Long donorId,
            @Valid @RequestBody FoodListing foodListing) {
        FoodListing created = foodListingService.createFoodListing(donorId, foodListing);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Feature 2: NGO browses available listings.
     */
    @GetMapping("/available")
    public ResponseEntity<List<FoodListing>> getAvailableListings() {
        List<FoodListing> listings = foodListingService.getAvailableListings();
        return ResponseEntity.ok(listings);
    }

    @GetMapping
    public ResponseEntity<List<FoodListing>> getAllFoodListings() {
        List<FoodListing> listings = foodListingService.getAllFoodListings();
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodListing> getFoodListingById(@PathVariable Long id) {
        FoodListing listing = foodListingService.getFoodListingById(id);
        return ResponseEntity.ok(listing);
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<FoodListing>> getListingsByDonor(@PathVariable Long donorId) {
        List<FoodListing> listings = foodListingService.getListingsByDonor(donorId);
        return ResponseEntity.ok(listings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodListing> updateFoodListing(
            @PathVariable Long id,
            @Valid @RequestBody FoodListing foodListing) {
        FoodListing updated = foodListingService.updateFoodListing(id, foodListing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodListing(@PathVariable Long id) {
        foodListingService.deleteFoodListing(id);
        return ResponseEntity.noContent().build();
    }
}
