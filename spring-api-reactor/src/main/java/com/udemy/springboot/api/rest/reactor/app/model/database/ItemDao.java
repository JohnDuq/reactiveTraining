package com.udemy.springboot.api.rest.reactor.app.model.database;

import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemDao extends ReactiveMongoRepository<Item, String> {

}
