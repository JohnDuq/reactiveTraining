package com.udemy.springboot.api.rest.reactor.app.service;

import com.udemy.springboot.api.rest.reactor.app.model.documents.Brand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBrandService {

    public Flux<Brand> findAll();

    public Mono<Brand> findById(String id);

    public Mono<Brand> save(Brand type);

}
