package com.udemy.springboot.webflux.app.model.database;

import com.udemy.springboot.webflux.app.model.documents.Brand;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BrandDao extends ReactiveMongoRepository<Brand, String> {
    
}
