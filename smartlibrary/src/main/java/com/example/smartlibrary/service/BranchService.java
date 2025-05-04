package com.example.smartlibrary.service;

import com.example.smartlibrary.model.Branch;
import com.example.smartlibrary.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    @Autowired
    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    public Branch createBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public void deleteBranchById(Long id) {
        branchRepository.deleteById(id);
    }
}
