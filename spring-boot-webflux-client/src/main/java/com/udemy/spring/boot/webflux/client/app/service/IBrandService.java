package com.udemy.spring.boot.webflux.client.app.service;

import com.udemy.spring.boot.webflux.client.app.model.documents.Brand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBrandService {

    public Flux<Brand> findAll();

    public Mono<Brand> findById(String id);

    public Mono<Brand> save(Brand type);

    public Mono<Brand> findByName(String name);

}
