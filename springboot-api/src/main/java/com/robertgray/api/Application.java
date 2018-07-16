package com.robertgray.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
@SpringBootApplication
public class Application {

	//Main method for springboot run
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
