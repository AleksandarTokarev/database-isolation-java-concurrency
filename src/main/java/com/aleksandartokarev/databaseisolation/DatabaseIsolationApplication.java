package com.aleksandartokarev.databaseisolation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatabaseIsolationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseIsolationApplication.class, args);
	}

}
