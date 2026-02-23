package com.yagnik.hospito.chain.service.impl;

import com.yagnik.hospito.chain.dto.ChainResponse;
import com.yagnik.hospito.chain.dto.CreateChainRequest;
import com.yagnik.hospito.chain.entity.HospitalChain;
import com.yagnik.hospito.chain.repository.ChainRepository;
import com.yagnik.hospito.chain.service.ChainService;
import com.yagnik.hospito.common.exception.BusinessRuleException;
import com.yagnik.hospito.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChainServiceImpl implements ChainService {

    private final ChainRepository chainRepository;

    @Override
    public ChainResponse createChain(CreateChainRequest request) {
        // Only one chain allowed in the system
        if (chainRepository.count() > 0) {
            throw new BusinessRuleException(
                "A hospital chain already exists. Use update to modify it.");
        }

        if (chainRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException(
                "Email already in use: " + request.getEmail());
        }

        if (chainRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BusinessRuleException(
                "Registration number already exists: " + request.getRegistrationNumber());
        }

        HospitalChain chain = HospitalChain.builder()
                .name(request.getName())
                .registrationNumber(request.getRegistrationNumber())
                .foundedYear(request.getFoundedYear())
                .headOfficeAddress(request.getHeadOfficeAddress())
                .email(request.getEmail())
                .contactPhone(request.getContactPhone())
                .description(request.getDescription())
                .build();

        return mapToResponse(chainRepository.save(chain));
    }

    @Override
    public ChainResponse getChain() {
        HospitalChain chain = chainRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No hospital chain found. Please create one first."));
        return mapToResponse(chain);
    }

    @Override
    public ChainResponse updateChain(CreateChainRequest request) {
        HospitalChain chain = chainRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No hospital chain found. Please create one first."));

        chain.setName(request.getName());
        chain.setRegistrationNumber(request.getRegistrationNumber());
        chain.setFoundedYear(request.getFoundedYear());
        chain.setHeadOfficeAddress(request.getHeadOfficeAddress());
        chain.setEmail(request.getEmail());
        chain.setContactPhone(request.getContactPhone());
        chain.setDescription(request.getDescription());

        return mapToResponse(chainRepository.save(chain));
    }

    private ChainResponse mapToResponse(HospitalChain chain) {
        return ChainResponse.builder()
                .id(chain.getId())
                .name(chain.getName())
                .registrationNumber(chain.getRegistrationNumber())
                .foundedYear(chain.getFoundedYear())
                .headOfficeAddress(chain.getHeadOfficeAddress())
                .email(chain.getEmail())
                .contactPhone(chain.getContactPhone())
                .description(chain.getDescription())
                .isActive(chain.isActive())
                .createdAt(chain.getCreatedAt())
                .build();
    }
}