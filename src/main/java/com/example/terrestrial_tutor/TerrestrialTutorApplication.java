package com.example.terrestrial_tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
@EnableGlobalMethodSecurity(securedEnabled = true)
@SpringBootApplication
public class TerrestrialTutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TerrestrialTutorApplication.class, args);
	}

}
