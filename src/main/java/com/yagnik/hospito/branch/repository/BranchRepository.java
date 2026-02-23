package com.yagnik.hospito.branch.repository;

import com.yagnik.hospito.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findAllByChainId(Long chainId);
    List<Branch> findAllByChainIdAndIsActive(Long chainId, boolean isActive);
    Optional<Branch> findByIdAndChainId(Long id, Long chainId);
    boolean existsByEmailAndChainId(String email, Long chainId);
}