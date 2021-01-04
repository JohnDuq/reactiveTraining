package com.udemy.springboot.api.rest.reactor.app.model.database;

import com.udemy.springboot.api.rest.reactor.app.model.documents.Brand;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface BrandDao extends ReactiveMongoRepository<Brand, String> {

    public Mono<Brand> findByName(String name);

}
