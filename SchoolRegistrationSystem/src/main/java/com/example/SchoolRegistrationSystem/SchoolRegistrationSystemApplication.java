package com.example.SchoolRegistrationSystem;

import com.example.SchoolRegistrationSystem.service.NodeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchoolRegistrationSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(SchoolRegistrationSystemApplication.class, args);
		NodeService.getInstance().buildDatabaseSchema();
	}

}
