package com.foodshare.service;

import com.foodshare.entity.Donor;
import com.foodshare.exception.ResourceNotFoundException;
import com.foodshare.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonorService {

    @Autowired
    private DonorRepository donorRepository;

    public Donor createDonor(Donor donor) {
        return donorRepository.save(donor);
    }

    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public Donor getDonorById(Long id) {
        return donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + id));
    }

    public Donor updateDonor(Long id, Donor donorDetails) {
        Donor donor = getDonorById(id);
        donor.setName(donorDetails.getName());
        donor.setEmail(donorDetails.getEmail());
        donor.setPhone(donorDetails.getPhone());
        donor.setOrganization(donorDetails.getOrganization());
        donor.setAddress(donorDetails.getAddress());
        return donorRepository.save(donor);
    }

    public void deleteDonor(Long id) {
        Donor donor = getDonorById(id);
        donorRepository.delete(donor);
    }
}
