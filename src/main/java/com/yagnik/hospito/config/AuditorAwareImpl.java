package com.yagnik.hospito.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // For now returns "system" — in Phase 1 (JWT Auth)
        // this will pull the username from SecurityContext
        return Optional.of("system");
    }
}