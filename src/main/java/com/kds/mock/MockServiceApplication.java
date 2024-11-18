package com.kds.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kds.mock")
public class MockServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MockServiceApplication.class, args);
	}

}
