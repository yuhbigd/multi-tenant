package com.example.multitenants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class MultiTenantsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiTenantsApplication.class, args);
	}

}
