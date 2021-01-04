package com.udemy.springboot.api.rest.reactor.app.model.database;

import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface ItemDao extends ReactiveMongoRepository<Item, String> {

    public Mono<Item> findByName(String name);

    @Query("{ 'name' : ?0 }")
    public Mono<Item> buscarPorNombre(String name);

}
