package com.foodshare.controller;

import com.foodshare.entity.NGO;
import com.foodshare.service.NGOService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ngos")
public class NGOController {

    @Autowired
    private NGOService ngoService;

    @PostMapping
    public ResponseEntity<NGO> createNGO(@Valid @RequestBody NGO ngo) {
        NGO createdNGO = ngoService.createNGO(ngo);
        return new ResponseEntity<>(createdNGO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NGO>> getAllNGOs() {
        List<NGO> ngos = ngoService.getAllNGOs();
        return ResponseEntity.ok(ngos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NGO> getNGOById(@PathVariable Long id) {
        NGO ngo = ngoService.getNGOById(id);
        return ResponseEntity.ok(ngo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NGO> updateNGO(@PathVariable Long id, @Valid @RequestBody NGO ngo) {
        NGO updatedNGO = ngoService.updateNGO(id, ngo);
        return ResponseEntity.ok(updatedNGO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNGO(@PathVariable Long id) {
        ngoService.deleteNGO(id);
        return ResponseEntity.noContent().build();
    }
}
