package com.udemy.springboot.api.rest.reactor.app.handler;

import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;
import com.udemy.springboot.api.rest.reactor.app.service.IItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Controller
public class ItemHandler {

    @Autowired
    private IItemService iItemService;

    public Mono<ServerResponse> itemList(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(iItemService.findAll(),
                Item.class);
    }

}
