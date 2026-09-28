package com.foodshare.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ngos")
public class NGO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    @Column(unique = true)
    private String registrationNumber;

    @NotBlank
    private String address;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "ngo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Claim> claims;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
