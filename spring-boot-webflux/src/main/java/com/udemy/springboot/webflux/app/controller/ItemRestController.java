package com.udemy.springboot.webflux.app.controller;

import com.udemy.springboot.webflux.app.model.database.ItemDao;
import com.udemy.springboot.webflux.app.model.documents.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/item")
public class ItemRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemRestController.class);

    @Autowired
    private ItemDao itemDao;

    @GetMapping
    public Flux<Item> index() {
        Flux<Item> flxItem = itemDao.findAll().map(item -> {
            item.setName(item.getName().toUpperCase());
            return item;
        }).doOnNext(item -> LOGGER.info(item.toString()));
        return flxItem;
    }

    @GetMapping("/{id}")
    public Mono<Item> getItemId(@PathVariable("id") String id) {
        //return itemDao.findById(id);
        Mono<Item> mnItem = itemDao.findAll()
        .filter(item -> item.getId().equals(id))
        .next() //Convierte el Flux a un Mono
        .doOnNext(item -> LOGGER.info(item.toString()));
        return mnItem;
    }

}
