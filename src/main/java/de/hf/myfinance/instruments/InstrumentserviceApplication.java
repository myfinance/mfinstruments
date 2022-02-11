package de.hf.myfinance.instruments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("de.hf")
public class InstrumentserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstrumentserviceApplication.class, args);
	}

}
