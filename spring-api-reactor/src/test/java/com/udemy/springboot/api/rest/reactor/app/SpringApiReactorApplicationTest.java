package com.udemy.springboot.api.rest.reactor.app;

import java.util.Collections;
import java.util.List;

import com.udemy.springboot.api.rest.reactor.app.common.Path;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Brand;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;
import com.udemy.springboot.api.rest.reactor.app.service.IBrandService;
import com.udemy.springboot.api.rest.reactor.app.service.IItemService;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringApiReactorApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private IItemService iItemService;

    @Autowired
    private IBrandService iBrandService;

    @Test
    void itemList() {
        webTestClient
            .get()
            .uri(Path.API_ITEM)
            .accept(MediaType.APPLICATION_JSON).exchange()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk()
            .expectBodyList(Item.class);
            //.hasSize(4);
    }

    @Test
    void itemList2() {
        webTestClient
            .get()
            .uri(Path.API_ITEM)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk()
            .expectBodyList(Item.class)
                .consumeWith(response -> {
                    List<Item> lItems = response.getResponseBody();
                    lItems.forEach(item -> {
                        System.out.println(item.toString());
                    });
                    Assertions.assertThat(lItems.size() > 0).isTrue();
                });
    }

    @Test
    void getItem() {
        String name = "PLAYSTATION 5";
        Item itemFind = iItemService.buscarPorNombre(name).block();
        webTestClient
            .get()
            .uri(Path.API_ITEM.concat(Path.ID), Collections.singletonMap("id", itemFind.getId()))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(name);
    }

    @Test
    void getItem2() {
        String name = "PLAYSTATION 5";
        Item itemFind = iItemService.buscarPorNombre(name).block();
        webTestClient.get()
                .uri(Path.API_ITEM.concat(Path.ID), 
                    Collections.singletonMap("id", itemFind.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody(Item.class)
                    .consumeWith(response -> {
                        Item item = response.getResponseBody();
                        Assertions.assertThat(item.getId()).isNotEmpty();
                        Assertions.assertThat(item.getName()).isEqualTo(name);
                    });
    }

    @Test
	void postItem() {
        Brand brand = iBrandService.findByName("SONY").block();
        Item item = new Item("TEST", 1234567980d, brand);
        webTestClient
			.post()
            .uri(Path.API_ITEM)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
			.exchange()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectStatus().isCreated()
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(item.getName())
                .jsonPath("$.price").isEqualTo(item.getPrice())
                .jsonPath("$.brand.id", item.getBrand().getId());
    }

    @Test
	void postItem2() {
        Brand brand = iBrandService.findByName("SONY").block();
        Item item = new Item("TEST", 1234567980d, brand);
        webTestClient
			.post()
            .uri(Path.API_ITEM)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
			.exchange()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectStatus().isCreated()
            .expectBody(Item.class)
                .consumeWith(response -> {
                    Item itemResponse = response.getResponseBody();
                    Assertions.assertThat(itemResponse.getId()).isNotEmpty();
                    Assertions.assertThat(itemResponse.getName()).isEqualTo(item.getName());
                    Assertions.assertThat(itemResponse.getPrice()).isEqualTo(item.getPrice());
                    Assertions.assertThat(itemResponse.getBrand().getId()).isEqualTo(item.getBrand().getId());
                });
    }

    @Test
	void putItem() {
        String name = "XBOX SERIES X";
        Item item = iItemService.buscarPorNombre(name).block();
        item.setName("EDITADO");
        item.setPrice(54321d);

        webTestClient
			.put()
            .uri(Path.API_ITEM.concat(Path.ID), Collections.singletonMap("id", item.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
			.exchange()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectStatus().isCreated()
            .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo(item.getName())
                .jsonPath("$.price").isEqualTo(item.getPrice())
                .jsonPath("$.brand.id", item.getBrand().getId());
    }

    @Test
	void putItem2() {
        String name = "XBOX 360";
        Item item = iItemService.buscarPorNombre(name).block();
        item.setName("EDITADO");
        item.setPrice(54321d);

        webTestClient
			.put()
            .uri(Path.API_ITEM.concat(Path.ID), Collections.singletonMap("id", item.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(item), Item.class)
			.exchange()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectStatus().isCreated()
            .expectBody(Item.class)
                .consumeWith(response -> {
                    Item itemResponse = response.getResponseBody();
                    Assertions.assertThat(itemResponse.getId()).isNotEmpty();
                    Assertions.assertThat(itemResponse.getName()).isEqualTo(item.getName());
                    Assertions.assertThat(itemResponse.getPrice()).isEqualTo(item.getPrice());
                    Assertions.assertThat(itemResponse.getBrand().getId()).isEqualTo(item.getBrand().getId());
                });
    }

    @Test
	void deleteItem() {
        String name = "PLAYSTATION 4";
        Item item = iItemService.buscarPorNombre(name).block();

        webTestClient
			.delete()
            .uri(Path.API_ITEM.concat(Path.ID), Collections.singletonMap("id", item.getId()))
			.exchange()
            .expectStatus().isNoContent()
            .expectBody().isEmpty();
        
        webTestClient.get()
            .uri(Path.API_ITEM.concat(Path.ID), Collections.singletonMap("id", item.getId()))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody().isEmpty();
    }

}
