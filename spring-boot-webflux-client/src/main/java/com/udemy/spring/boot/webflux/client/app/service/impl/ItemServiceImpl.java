package com.udemy.spring.boot.webflux.client.app.service.impl;

import java.util.Collections;

import com.udemy.spring.boot.webflux.client.app.common.Path;
import com.udemy.spring.boot.webflux.client.app.model.documents.Item;
import com.udemy.spring.boot.webflux.client.app.service.IItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private WebClient webClient;

    @Override
    public Flux<Item> findAll() {
        return webClient.get()
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToFlux(response -> response.bodyToFlux(Item.class));
    }

    @Override
    public Mono<Item> findById(String id) {
        return webClient.get()
            .uri(Path.ID, Collections.singletonMap("id", id))
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(response -> response.bodyToMono(Item.class));
    }

    @Override
    public Mono<Item> save(Item item) {
        return webClient.post()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(item)
            .exchangeToMono(response -> response.bodyToMono(Item.class));
    }

    @Override
    public Mono<Item> update(Item item, String id) {
        return webClient.put()
            .uri(Path.ID, Collections.singletonMap("id", id))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(item)
            .exchangeToMono(response -> response.bodyToMono(Item.class));
    }

    @Override
    public Mono<Void> delete(String id) {
        return webClient.delete()
            .uri(Path.ID, Collections.singletonMap("id", id))
            .exchange()
            .then();
    }

}
