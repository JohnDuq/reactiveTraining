package com.udemy.springboot.webflux.app.model.database;

import com.udemy.springboot.webflux.app.model.documents.Item;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemDao extends ReactiveMongoRepository<Item, String> {
    
}
