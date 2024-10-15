package com.bkleszcz.WordApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class WordAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(WordAppApplication.class, args);
	}

}
