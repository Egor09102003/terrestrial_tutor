package com.example.terrestrial_tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity
@SpringBootApplication
public class TerrestrialTutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TerrestrialTutorApplication.class, args);
	}

}
