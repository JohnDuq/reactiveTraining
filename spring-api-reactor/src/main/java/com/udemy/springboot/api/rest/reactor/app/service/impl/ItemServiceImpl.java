package com.udemy.springboot.api.rest.reactor.app.service.impl;

import com.udemy.springboot.api.rest.reactor.app.model.database.ItemDao;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;
import com.udemy.springboot.api.rest.reactor.app.service.IItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private ItemDao itemDao;

    @Override
    public Flux<Item> findAll() {
        return itemDao.findAll();
    }

    @Override
    public Flux<Item> findAllNameUpper() {
        Flux<Item> flxItem = itemDao.findAll().map(item -> {
            item.setName(item.getName().toUpperCase());
            return item;
        });
        return flxItem;
    }

    @Override
    public Flux<Item> findAllNameUpperRepeat() {
        return findAllNameUpper().repeat(5000);
    }

    @Override
    public Mono<Item> findById(String id) {
        return itemDao.findById(id);
    }

    @Override
    public Mono<Item> save(Item item) {
        return itemDao.save(item);
    }

    @Override
    public Mono<Void> delete(Item item) {
        return itemDao.delete(item);
    }

    @Override
    public Mono<Void> delete(String id) {
        return itemDao.deleteById(id);
    }

    @Override
    public Mono<Item> findByName(String name) {
        return itemDao.findByName(name);
    }

    @Override
    public Mono<Item> buscarPorNombre(String name) {
        return itemDao.buscarPorNombre(name);
    }

}
