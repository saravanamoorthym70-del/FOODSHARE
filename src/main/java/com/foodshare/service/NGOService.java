package com.foodshare.service;

import com.foodshare.entity.NGO;
import com.foodshare.exception.ResourceNotFoundException;
import com.foodshare.repository.NGORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NGOService {

    @Autowired
    private NGORepository ngoRepository;

    public NGO createNGO(NGO ngo) {
        return ngoRepository.save(ngo);
    }

    public List<NGO> getAllNGOs() {
        return ngoRepository.findAll();
    }

    public NGO getNGOById(Long id) {
        return ngoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NGO not found with id: " + id));
    }

    public NGO updateNGO(Long id, NGO ngoDetails) {
        NGO ngo = getNGOById(id);
        ngo.setName(ngoDetails.getName());
        ngo.setEmail(ngoDetails.getEmail());
        ngo.setPhone(ngoDetails.getPhone());
        ngo.setRegistrationNumber(ngoDetails.getRegistrationNumber());
        ngo.setAddress(ngoDetails.getAddress());
        return ngoRepository.save(ngo);
    }

    public void deleteNGO(Long id) {
        NGO ngo = getNGOById(id);
        ngoRepository.delete(ngo);
    }
}
