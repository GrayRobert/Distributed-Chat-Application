package com.robertgray.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan()
@MapperScan("com.robertgray.api.mappers")
@SpringBootApplication
public class Application {

	//Main method for springboot run
	public static void main(String[] args) {
		ApplicationContext applicationContext =SpringApplication.run(Application.class, args);

		for (String name: applicationContext.getBeanDefinitionNames()) {
			System.out.println(name);
		}
	}
}
