package com.testlibrary.testlibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TestLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestLibraryApplication.class, args);
	}

}
