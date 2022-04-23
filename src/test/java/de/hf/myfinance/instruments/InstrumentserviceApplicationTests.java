package de.hf.myfinance.instruments;

import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class InstrumentserviceApplicationTests extends MongoDbTestBase{

	@Autowired
	private WebTestClient client;

	@Autowired
	InstrumentRepository instrumentRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void getTenantById() {
		var tenant = new Instrument("testTenant", InstrumentType.TENANT);
		postAndVerifyTenant(tenant, OK);
		var savedTenant = instrumentRepository.findByBusinesskey("testTenant@6");

		getAndVerifyInstrument(savedTenant.getBusinesskey(), OK).jsonPath("$.description").isEqualTo(tenant.getDescription());
	}

	private WebTestClient.BodyContentSpec postAndVerifyTenant(Instrument tenant, HttpStatus expectedStatus) {

		return client.post()
				.uri("/addinstrument")
				.body(just(tenant), Instrument.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				//.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec getAndVerifyInstrument(String businesskey, HttpStatus expectedStatus) {
		return client.get()
				.uri("/instrument/"+businesskey)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}



}
