package com.foodshare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime claimedAt;
    private LocalDateTime collectedAt;

    private String status = "ACTIVE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    @JsonIgnoreProperties({"claims", "hibernateLazyInitializer", "handler"})
    private NGO ngo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_listing_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"claim", "hibernateLazyInitializer", "handler"})
    private FoodListing foodListing;

    @PrePersist
    protected void onCreate() {
        this.claimedAt = LocalDateTime.now();
    }
}
