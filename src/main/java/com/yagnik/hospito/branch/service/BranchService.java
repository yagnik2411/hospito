package com.yagnik.hospito.branch.service;

import com.yagnik.hospito.branch.dto.BranchResponse;
import com.yagnik.hospito.branch.dto.CreateBranchRequest;

import java.util.List;

public interface BranchService {
    BranchResponse createBranch(CreateBranchRequest request);
    BranchResponse getBranchById(Long id);
    List<BranchResponse> getAllBranches();
    BranchResponse updateBranch(Long id, CreateBranchRequest request);
    void deleteBranch(Long id);
}