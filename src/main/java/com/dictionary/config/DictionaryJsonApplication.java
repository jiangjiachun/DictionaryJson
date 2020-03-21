package com.dictionary.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dictionary.controller", "com.dictionary.config"}) 
public class DictionaryJsonApplication {

	public static void main(String[] args) {
		SpringApplication.run(DictionaryJsonApplication.class, args);
	}
		

}
