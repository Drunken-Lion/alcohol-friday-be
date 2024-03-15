package com.drunkenlion.alcoholfriday;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@OpenAPIDefinition(servers = {
		@Server(url = "/", description = "https://api.alcoholfriday.store"),
		@Server(url = "/", description = "https://api.alcoholfriday.shop")
})
@SpringBootApplication
@EnableJpaAuditing
public class AlcoholfridayApplication {
	public static void main(String[] args) {
		SpringApplication.run(AlcoholfridayApplication.class, args);
	}
}
