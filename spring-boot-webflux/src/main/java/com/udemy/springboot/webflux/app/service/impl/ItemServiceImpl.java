package com.udemy.springboot.webflux.app.service.impl;

import com.udemy.springboot.webflux.app.model.database.ItemDao;
import com.udemy.springboot.webflux.app.model.documents.Item;
import com.udemy.springboot.webflux.app.service.IItemService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemServiceImpl implements IItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

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

}
