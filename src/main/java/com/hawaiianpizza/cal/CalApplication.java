package com.hawaiianpizza.cal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
public class CalApplication {
	public static void main(String[] args) {
		SpringApplication.run(CalApplication.class, args);
	}

}
