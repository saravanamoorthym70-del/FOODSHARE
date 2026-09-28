package com.foodshare.controller;

import com.foodshare.entity.Donor;
import com.foodshare.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    @Autowired
    private DonorService donorService;

    @PostMapping
    public ResponseEntity<Donor> createDonor(@Valid @RequestBody Donor donor) {
        Donor createdDonor = donorService.createDonor(donor);
        return new ResponseEntity<>(createdDonor, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Donor>> getAllDonors() {
        List<Donor> donors = donorService.getAllDonors();
        return ResponseEntity.ok(donors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donor> getDonorById(@PathVariable Long id) {
        Donor donor = donorService.getDonorById(id);
        return ResponseEntity.ok(donor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Donor> updateDonor(@PathVariable Long id, @Valid @RequestBody Donor donor) {
        Donor updatedDonor = donorService.updateDonor(id, donor);
        return ResponseEntity.ok(updatedDonor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonor(@PathVariable Long id) {
        donorService.deleteDonor(id);
        return ResponseEntity.noContent().build();
    }
}
