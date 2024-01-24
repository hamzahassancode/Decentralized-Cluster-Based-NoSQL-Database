package com.example.Bootstrap;

import com.example.Bootstrap.repo.BootstrapRepo;
import com.example.Bootstrap.service.BootstrapService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootstrapApplication {

	public static void main(String[] args) {

		SpringApplication.run(BootstrapApplication.class, args);
		BootstrapRepo.getInstance().setUpNodes();

	}

}
