package de.hf.myfinance.restapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import de.hf.myfinance.restmodel.Instrument;


public interface InstrumentService {

	@GetMapping("/")
	String index();

	@GetMapping(value = "/instrument/{instrumentId}", produces = "application/json") 
	Instrument getProduct(@PathVariable int instrumentId);
}