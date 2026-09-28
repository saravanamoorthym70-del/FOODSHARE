package com.foodshare.controller;

import com.foodshare.entity.Claim;
import com.foodshare.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    /**
     * Feature 2: NGO claims an available listing.
     * POST /api/claims/listing/{foodListingId}/ngo/{ngoId}
     */
    @PostMapping("/listing/{foodListingId}/ngo/{ngoId}")
    public ResponseEntity<Claim> claimListing(
            @PathVariable Long foodListingId,
            @PathVariable Long ngoId) {
        Claim claim = claimService.claimListing(foodListingId, ngoId);
        return new ResponseEntity<>(claim, HttpStatus.CREATED);
    }

    /**
     * Feature 3: Mark a listing collected once picked up.
     * PUT /api/claims/{claimId}/collect
     */
    @PutMapping("/{claimId}/collect")
    public ResponseEntity<Claim> markAsCollected(@PathVariable Long claimId) {
        Claim claim = claimService.markAsCollected(claimId);
        return ResponseEntity.ok(claim);
    }

    /**
     * Cancel an active claim, making the listing available again.
     * PUT /api/claims/{claimId}/cancel
     */
    @PutMapping("/{claimId}/cancel")
    public ResponseEntity<Claim> cancelClaim(@PathVariable Long claimId) {
        Claim claim = claimService.cancelClaim(claimId);
        return ResponseEntity.ok(claim);
    }

    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        List<Claim> claims = claimService.getAllClaims();
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        Claim claim = claimService.getClaimById(id);
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<List<Claim>> getClaimsByNGO(@PathVariable Long ngoId) {
        List<Claim> claims = claimService.getClaimsByNGO(ngoId);
        return ResponseEntity.ok(claims);
    }
}
