package com.example.multitenants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableMethodSecurity
public class MultiTenantsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiTenantsApplication.class, args);
	}

}
