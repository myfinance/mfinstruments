package de.hf.myfinance.mfinstrument;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("de.hf.myfinance")
public class InstrumentserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstrumentserviceApplication.class, args);
	}

}
