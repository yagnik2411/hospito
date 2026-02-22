package com.yagnik.hospito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class HospitoApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitoApplication.class, args);
	}
}