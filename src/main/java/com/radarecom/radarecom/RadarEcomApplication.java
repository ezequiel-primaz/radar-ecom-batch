package com.radarecom.radarecom;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class RadarEcomApplication {

	public static void main(String[] args) {
		SpringApplication.run(RadarEcomApplication.class, args);
	}

	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("Brazil/East"));
	}

}
