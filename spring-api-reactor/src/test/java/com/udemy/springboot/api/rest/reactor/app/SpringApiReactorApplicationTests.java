package com.udemy.springboot.api.rest.reactor.app;

import com.udemy.springboot.api.rest.reactor.app.common.DataCommon;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringApiReactorApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getItems() {
		webTestClient
			.get()
			.uri(DataCommon.COLLECTION_API_ITEM)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectStatus().isOk()
			.expectBodyList(Item.class).hasSize(4);
	}

}
