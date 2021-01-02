package com.udemy.springboot.webflux.app.service;

import com.udemy.springboot.webflux.app.model.documents.Brand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBrandService {
    
    public Flux<Brand> findAll();
    public Mono<Brand> findById(String id);
    public Mono<Brand> save(Brand type);

}
