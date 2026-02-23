package com.yagnik.hospito.branch.service.impl;

import com.yagnik.hospito.branch.dto.BranchResponse;
import com.yagnik.hospito.branch.dto.CreateBranchRequest;
import com.yagnik.hospito.branch.entity.Branch;
import com.yagnik.hospito.branch.repository.BranchRepository;
import com.yagnik.hospito.branch.service.BranchService;
import com.yagnik.hospito.chain.entity.HospitalChain;
import com.yagnik.hospito.chain.repository.ChainRepository;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final ChainRepository chainRepository;

    @Override
    public BranchResponse createBranch(CreateBranchRequest request) {
        // Chain must exist before creating a branch
        HospitalChain chain = chainRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No hospital chain found. Create a chain first."));

        // Check duplicate email within same chain
        if (request.getEmail() != null &&
            branchRepository.existsByEmailAndChainId(request.getEmail(), chain.getId())) {
            throw new BusinessRuleException(
                "A branch with this email already exists in the chain.");
        }

        Branch branch = Branch.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .contactPhone(request.getContactPhone())
                .email(request.getEmail())
                .chain(chain)
                .build();

        return mapToResponse(branchRepository.save(branch));
    }

    @Override
    public BranchResponse getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));
        return mapToResponse(branch);
    }

    @Override
    public List<BranchResponse> getAllBranches() {
        HospitalChain chain = chainRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No hospital chain found."));
        return branchRepository.findAllByChainId(chain.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BranchResponse updateBranch(Long id, CreateBranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));

        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setState(request.getState());
        branch.setContactPhone(request.getContactPhone());
        branch.setEmail(request.getEmail());

        return mapToResponse(branchRepository.save(branch));
    }

    @Override
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));

        // Soft delete — just mark inactive
        branch.setActive(false);
        branchRepository.save(branch);
    }

    private BranchResponse mapToResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .city(branch.getCity())
                .state(branch.getState())
                .contactPhone(branch.getContactPhone())
                .email(branch.getEmail())
                .isActive(branch.isActive())
                .chainId(branch.getChain().getId())
                .chainName(branch.getChain().getName())
                .createdAt(branch.getCreatedAt())
                .build();
    }
}