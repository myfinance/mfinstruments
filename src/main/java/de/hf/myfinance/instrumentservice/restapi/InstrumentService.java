package de.hf.myfinance.instrumentservice.restapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstrumentService {
	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}
}