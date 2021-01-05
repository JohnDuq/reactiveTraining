package com.udemy.spring.boot.webflux.client.app.handler;

import com.udemy.spring.boot.webflux.client.app.model.documents.Item;
import com.udemy.spring.boot.webflux.client.app.service.IItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class ItemHandler {

    @Autowired
    private IItemService iItemService;

    public Mono<ServerResponse> findAll(ServerRequest serverRequest){
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(iItemService.findAll(), Item.class);
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        return iItemService.findById(id)
            .flatMap(item -> 
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(item))
            .switchIfEmpty(ServerResponse.notFound().build());
    }


}
