package com.udemy.spring.boot.webflux.client.app.service;

import com.udemy.spring.boot.webflux.client.app.model.documents.Item;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IItemService {

    public Flux<Item> findAll();

    public Mono<Item> findById(String id);

    public Mono<Item> save(Item item);

    public Mono<Item> update(Item item, String id);

    public Mono<Void> delete(String id);

    public Mono<Item> upload(FilePart filePart, String id);

}
