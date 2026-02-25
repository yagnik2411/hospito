package com.yagnik.hospito.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hospito API")
                        .description("""
                                Hospito is an enterprise-grade hospital franchise management platform \
                                designed to streamline operations across multiple branches under a single \
                                hospital chain.

                                The platform supports the complete patient journey — from registration \
                                and appointment booking to consultation, medical records, and billing — \
                                all within a unified, multi-branch ecosystem.

                                Core Capabilities:

                                • Multi-Branch Operations
                                  Manage an unlimited number of hospital branches under one chain. \
                                  Doctors can be assigned to and transferred across branches. \
                                  Patient records are shared across the entire chain.

                                • Appointment Management
                                  Patients book appointments with doctors at specific branches. \
                                  The system enforces conflict detection to prevent double-booking \
                                  and tracks appointment lifecycle from PENDING through COMPLETED.

                                • Medical Records
                                  Doctors record diagnoses, prescriptions, and clinical notes \
                                  against each patient visit. Full history is available across branches.

                                • Billing and Payments
                                  Bills are generated against completed appointments with line-item \
                                  breakdown of services. Payments are accepted via Cash, Card, UPI, \
                                  and Insurance with automatic coverage calculation.

                                • Access Control
                                  Every endpoint is protected by role-based access control. \
                                  Four roles are supported: SUPER_ADMIN, BRANCH_ADMIN, DOCTOR, and PATIENT. \
                                  Each role has a precisely defined set of permitted operations.

                                Authentication:
                                  Click the Authorize button, obtain a token from POST /api/v1/auth/login, \
                                  and paste it to authenticate all requests in this UI.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Yagnik")
                                .email("yagnik@hospito.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                                "Paste your JWT token here. Get it from POST /api/v1/auth/login")));
    }
}