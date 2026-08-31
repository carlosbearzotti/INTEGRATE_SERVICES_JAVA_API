package com.desafio.integrados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IntegradosApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegradosApplication.class, args);
	}

}
