package com.udemy.spring.boot.webflux.client.app.service.impl;

import java.util.Collections;

import com.udemy.spring.boot.webflux.client.app.common.Path;
import com.udemy.spring.boot.webflux.client.app.model.documents.Item;
import com.udemy.spring.boot.webflux.client.app.service.IItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    private WebClient webClient;

    @Override
    public Flux<Item> findAll() {
        return webClient.get()
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(Item.class);
    }

    @Override
    public Mono<Item> findById(String id) {
        return webClient.get()
            .uri(Path.ID, Collections.singletonMap("id", id))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Item.class);
    }

    @Override
    public Mono<Item> save(Item item) {
        return webClient.post()
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(item)
            .retrieve()
            .bodyToMono(Item.class);
    }

    @Override
    public Mono<Item> update(Item item, String id) {
        return webClient.put()
            .uri(Path.ID, Collections.singletonMap("id", id))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(item)
            .retrieve()
            .bodyToMono(Item.class);
    }

    @Override
    public Mono<Void> delete(String id) {
        return webClient.delete()
            .uri(Path.ID, Collections.singletonMap("id", id))
            .retrieve()
            .bodyToMono(Void.class);
    }

	@Override
	public Mono<Item> upload(FilePart filePart, String id) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.asyncPart("filePart", filePart.content(), DataBuffer.class)
            .headers(h -> {
                h.setContentDispositionFormData("filePart", filePart.filename());
            });
        return webClient.post()
            .uri(Path.UPLOAD_ID, Collections.singletonMap("id", id))
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .bodyValue(multipartBodyBuilder.build())
            .retrieve()
            .bodyToMono(Item.class);
	}

}
