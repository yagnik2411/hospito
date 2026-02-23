package com.yagnik.hospito.chain.service;

import com.yagnik.hospito.chain.dto.ChainResponse;
import com.yagnik.hospito.chain.dto.CreateChainRequest;

public interface ChainService {
    ChainResponse createChain(CreateChainRequest request);
    ChainResponse getChain();
    ChainResponse updateChain(CreateChainRequest request);
}