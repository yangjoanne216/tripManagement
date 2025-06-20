package fr.dauphine.miageIf.minh.yang.info_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info = @Info(title = "info service backend",
		description = "info service endpoints and apis",
		contact = @Contact(name = "Yang", email = "yangyang2@dauphine.eu"),
		version = "2.0.0"))
public class InfoServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(InfoServiceApplication.class, args);
	}

}
