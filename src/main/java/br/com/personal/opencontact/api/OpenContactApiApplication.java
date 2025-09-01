package br.com.personal.opencontact.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class OpenContactApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenContactApiApplication.class, args);
	}

}
