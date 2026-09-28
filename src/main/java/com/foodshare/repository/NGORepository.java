package com.foodshare.repository;

import com.foodshare.entity.NGO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NGORepository extends JpaRepository<NGO, Long> {

    Optional<NGO> findByEmail(String email);

    Optional<NGO> findByRegistrationNumber(String registrationNumber);

    boolean existsByEmail(String email);

    boolean existsByRegistrationNumber(String registrationNumber);
}
