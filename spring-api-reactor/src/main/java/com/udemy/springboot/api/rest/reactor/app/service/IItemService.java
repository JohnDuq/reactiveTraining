package com.udemy.springboot.api.rest.reactor.app.service;

import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IItemService {

    public Flux<Item> findAll();

    public Flux<Item> findAllNameUpper();

    public Flux<Item> findAllNameUpperRepeat();

    public Mono<Item> findById(String id);

    public Mono<Item> save(Item item);

    public Mono<Void> delete(Item item);

    public Mono<Void> delete(String id);

    public Mono<Item> findByName(String name);

    public Mono<Item> buscarPorNombre(String name);

}
