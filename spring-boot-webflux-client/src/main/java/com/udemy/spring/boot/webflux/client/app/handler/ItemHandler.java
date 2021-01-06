package com.udemy.spring.boot.webflux.client.app.handler;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.udemy.spring.boot.webflux.client.app.common.Path;
import com.udemy.spring.boot.webflux.client.app.model.documents.Item;
import com.udemy.spring.boot.webflux.client.app.service.IItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
            //.switchIfEmpty(ServerResponse.notFound().build())
            .onErrorResume(WebClientResponseException.class, webClientResponseException -> {
                if(HttpStatus.NOT_FOUND.equals(webClientResponseException.getStatusCode())){
                    Map<String, Object> body = new HashMap<>();
                    body.put("error", "Item dont exist: ".concat(webClientResponseException.getMessage()));
                    body.put("timestamp", new Date());
                    body.put("status", webClientResponseException.getStatusCode().value());
                    return ServerResponse.status(HttpStatus.NOT_FOUND).syncBody(body);
                } else {
                    return Mono.error(webClientResponseException);
                }
            });
    }


    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        Mono<Item> item = serverRequest.bodyToMono(Item.class);
        return item.flatMap(iItemService::save)
            .flatMap(itemSaved -> 
                ServerResponse.created(URI.create(Path.API_CLIENT.concat(Path.SLASH).concat(itemSaved.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(itemSaved))
            .onErrorResume(WebClientResponseException.class, webClientResponseException -> {
                if(HttpStatus.BAD_REQUEST.equals(webClientResponseException.getStatusCode())){
                    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(webClientResponseException.getResponseBodyAsString());
                } else {
                    return Mono.error(webClientResponseException);
                }
            });
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemRequest = serverRequest.bodyToMono(Item.class);
        return itemRequest.flatMap(item -> iItemService.update(item, id))
            .flatMap(itemSaved -> 
                ServerResponse.created(URI.create(Path.API_CLIENT.concat(Path.SLASH).concat(id)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(itemSaved))
            .onErrorResume(WebClientResponseException.class, webClientResponseException -> {
                if(HttpStatus.NOT_FOUND.equals(webClientResponseException.getStatusCode())){
                    return ServerResponse.notFound().build();
                } else {
                    return Mono.error(webClientResponseException);
                }
            });
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        return iItemService.delete(serverRequest.pathVariable("id"))
            .then(ServerResponse.noContent().build())
            .onErrorResume(WebClientResponseException.class, webClientResponseException -> {
                if(HttpStatus.NOT_FOUND.equals(webClientResponseException.getStatusCode())){
                    return ServerResponse.notFound().build();
                } else {
                    return Mono.error(webClientResponseException);
                }
            });
    }

    public Mono<ServerResponse> upload(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.multipartData()
            .map(multiPart -> multiPart.toSingleValueMap().get("filePart"))
            .cast(FilePart.class)
            .flatMap(file -> iItemService.upload(file, id))
            .flatMap(itemSaved -> ServerResponse
                .created(URI.create(Path.API_CLIENT.concat(Path.SLASH).concat(itemSaved.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(itemSaved))
            .onErrorResume(WebClientResponseException.class, webClientResponseException -> {
                if(HttpStatus.NOT_FOUND.equals(webClientResponseException.getStatusCode())){
                    return ServerResponse.notFound().build();
                } else {
                    return Mono.error(webClientResponseException);
                }
            });
            
    }

}
