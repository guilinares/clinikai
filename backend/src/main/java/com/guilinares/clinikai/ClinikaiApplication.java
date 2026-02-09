package com.guilinares.clinikai;

import com.guilinares.clinikai.infrastructure.google.GoogleProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(GoogleProperties.class)
@SpringBootApplication
public class ClinikaiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClinikaiApplication.class, args);
	}
}
