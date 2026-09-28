package com.foodshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="food_listings")
public class FoodListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String foodName;

    @NotBlank
    private String foodType;

    @NotNull
    @Positive
    private Double quantity;

    @NotBlank
    private String unit;

    private String description;

    @NotNull
    @Future
    private LocalDateTime safeToEatUntil;

    private String status = "AVAILABLE";

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    @JsonIgnoreProperties({"foodListings", "hibernateLazyInitializer", "handler"})
    private Donor donor;

    @OneToOne(mappedBy = "foodListing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"foodListing", "hibernateLazyInitializer", "handler"})
    private Claim claim;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
