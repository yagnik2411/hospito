package com.yagnik.hospito.chain.repository;

import com.yagnik.hospito.chain.entity.HospitalChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChainRepository extends JpaRepository<HospitalChain, Long> {
    Optional<HospitalChain> findFirstByOrderByIdAsc();
    boolean existsByEmail(String email);
    boolean existsByRegistrationNumber(String registrationNumber);
}