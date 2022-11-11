package de.hf.myfinance.instruments;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hf.myfinance.event.Event;
import de.hf.myfinance.instruments.persistence.repositories.InstrumentRepository;
import de.hf.myfinance.restmodel.Instrument;
import de.hf.myfinance.restmodel.InstrumentType;
import de.hf.testhelper.JsonHelper;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static reactor.core.publisher.Mono.just;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@Import({TestChannelBinderConfiguration.class})
class InstrumentserviceApplicationTests extends EventProcessorTestBase {

	@Autowired
	private WebTestClient client;

	@Autowired
	InstrumentRepository instrumentRepository;

	@Autowired
	@Qualifier("validateInstrumentProcessor")
	private Consumer<Event<String, Instrument>> validateInstrumentProcessor;

	@Autowired
	@Qualifier("saveInstrumentProcessor")
	protected Consumer<Event<String, Instrument>> saveInstrumentProcessor;

	@Autowired
	@Qualifier("saveInstrumentTreeProcessor")
	protected Consumer<Event<String, Instrument>> saveInstrumentTreeProcessor;

	@Test
	void contextLoads() {
	}

	@Test
	void createTenantViaApi() {
		var tenant = new Instrument("testTenant", InstrumentType.TENANT);
		postAndVerifyTenant(tenant, OK);
		final List<String> messages = getMessages("instrumentApproved-out-0");
		assertEquals(5, messages.size());
	}

	@Test
	void createTenantViaMsg() {
		sendCreateInstrumentEvent("nextTenant", InstrumentType.TENANT);
		final List<String> messages = getMessages("instrumentApproved-out-0");
		assertEquals(5, messages.size());

	}

	@Test
	void checkGetApi() {
		var tenantKey = "aTest@6";
		var tenantDesc = "aTest";
		var newInstrument = new Instrument(tenantDesc, InstrumentType.TENANT);
		newInstrument.setBusinesskey(tenantKey);
		var creatEvent = new Event(Event.Type.CREATE, tenantKey, newInstrument);
		saveInstrumentProcessor.accept(creatEvent);
		saveInstrumentTreeProcessor.accept(creatEvent);
		getAndVerifyInstrument(tenantKey, OK).jsonPath("$.description").isEqualTo(tenantDesc);
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
		Event<String, Instrument> event = new Event(CREATE, desc, instrument);
		validateInstrumentProcessor.accept(event);
	}

}
