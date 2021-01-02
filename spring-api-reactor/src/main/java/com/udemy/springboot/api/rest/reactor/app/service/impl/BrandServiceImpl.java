package com.udemy.springboot.api.rest.reactor.app.service.impl;

import com.udemy.springboot.api.rest.reactor.app.model.database.BrandDao;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Brand;
import com.udemy.springboot.api.rest.reactor.app.service.IBrandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private BrandDao brandDao;

    @Override
    public Flux<Brand> findAll() {
        return brandDao.findAll();
    }

    @Override
    public Mono<Brand> findById(String id) {
        return brandDao.findById(id);
    }

    @Override
    public Mono<Brand> save(Brand type) {
        return brandDao.save(type);
    }

}
