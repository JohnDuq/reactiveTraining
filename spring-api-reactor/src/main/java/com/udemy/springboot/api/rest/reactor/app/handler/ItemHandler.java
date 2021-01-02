package com.udemy.springboot.api.rest.reactor.app.handler;

import java.net.URI;
import java.util.Date;

import com.udemy.springboot.api.rest.reactor.app.common.DataCommon;
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
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(iItemService.findAll(),
                Item.class);
    }

    public Mono<ServerResponse> itemDetail(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return iItemService.findById(id).flatMap(itemFind -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON).bodyValue(itemFind))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> itemSave(ServerRequest serverRequest) {
        Mono<Item> mnItem = serverRequest.bodyToMono(Item.class);
        return mnItem.flatMap(item -> {
            if (item.getCreateAt() == null)
                item.setCreateAt(new Date());
            return iItemService.save(item);
        }).flatMap(itemSaved -> ServerResponse
                .created(URI.create(DataCommon.COLLECTION_API_ITEM_VS2.concat("/").concat(itemSaved.getId())))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(itemSaved));
    }

}
