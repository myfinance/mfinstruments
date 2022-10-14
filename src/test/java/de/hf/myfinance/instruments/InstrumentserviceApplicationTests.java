package de.hf.myfinance.instruments;

import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.testhelper.MongoDbTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;


import static de.hf.myfinance.event.Event.Type.CREATE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import java.util.function.Consumer;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Import({TestChannelBinderConfiguration.class})
class InstrumentserviceApplicationTests extends MongoDbTestBase {

	@Autowired
	private WebTestClient client;

	@Autowired
	InstrumentRepository instrumentRepository;

	@Autowired
	@Qualifier("messageProcessor")
	private Consumer<Event<Integer, Instrument>> messageProcessor;

	@Test
	void contextLoads() {
	}

	@Test
	void getTenantById() {
		var tenant = new Instrument("testTenant", InstrumentType.TENANT);
		postAndVerifyTenant(tenant, OK);
		var savedTenant = instrumentRepository.findByBusinesskey("testTenant@6").block();

		getAndVerifyInstrument(savedTenant.getBusinesskey(), OK).jsonPath("$.description").isEqualTo(tenant.getDescription());
	}

	@Test
	void createTenantViaMsg() {
		sendCreateInstrumentEvent("nextTenant", InstrumentType.TENANT);
		var savedTenant = instrumentRepository.findByBusinesskey("nextTenant@6").block();

		getAndVerifyInstrument(savedTenant.getBusinesskey(), OK).jsonPath("$.description").isEqualTo("nextTenant");
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

	private void sendCreateInstrumentEvent(String desc, InstrumentType type) {
		var instrument = new Instrument(desc, type);
		Event<Integer, Instrument> event = new Event(CREATE, desc, instrument);
		messageProcessor.accept(event);
	}

}
