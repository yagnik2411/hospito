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
                                Hospital Franchise Management System

                                A multi-branch hospital management platform supporting:
                                - JWT Authentication with role-based access control
                                - Chain and Branch management
                                - Doctor scheduling and transfers
                                - Patient registration and medical records
                                - Appointment booking with conflict detection
                                - Billing with Cash, Card, UPI, and Insurance payment strategies

                                Roles: SUPER_ADMIN, BRANCH_ADMIN, DOCTOR, PATIENT
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