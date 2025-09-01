package br.com.personal.opencontact.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OpenContactApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenContactApiApplication.class, args);
	}

}
