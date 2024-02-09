package com.example.SecureCapitaInitializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication/*(exclude = { SecurityAutoConfiguration.class })*/
public class SecureCapitaInitializrApplication {

	private static final int STRENGTH = 10;

	public static void main(String[] args) {
		SpringApplication.run(SecureCapitaInitializrApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(STRENGTH); }
}
