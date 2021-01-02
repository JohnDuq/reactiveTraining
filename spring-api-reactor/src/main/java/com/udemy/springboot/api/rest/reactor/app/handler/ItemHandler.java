package com.udemy.springboot.api.rest.reactor.app.handler;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import com.udemy.springboot.api.rest.reactor.app.common.DataCommon;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Brand;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;
import com.udemy.springboot.api.rest.reactor.app.service.IItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Controller
public class ItemHandler {

    @Autowired
    private IItemService iItemService;
    @Value("${config.upload.path}")
    private String configUploadPath;

    public Mono<ServerResponse> itemList(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(iItemService.findAll(), Item.class);
    }

    public Mono<ServerResponse> itemDetail(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return iItemService.findById(id)
                .flatMap(itemFind -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(itemFind))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> itemPhotoUpload(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return serverRequest.multipartData()
        .map(multiPart -> multiPart.toSingleValueMap().get("filePart"))
        .cast(FilePart.class)
        .flatMap(filePart -> iItemService.findById(id).flatMap(itemFind -> {
            itemFind.setPhoto(UUID.randomUUID().toString().concat("-")
                    .concat(filePart.filename().replace(" ", "_").replace(":", "").replace("\\", "")));
            return filePart.transferTo(new File(configUploadPath.concat("/").concat(itemFind.getPhoto())))
                    .then(iItemService.save(itemFind));
            }))
        .flatMap(itemSaved -> ServerResponse
            .created(URI.create(DataCommon.COLLECTION_API_ITEM_VS2.concat("/").concat(itemSaved.getId())))
            .contentType(MediaType.APPLICATION_JSON).bodyValue(itemSaved))
        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> itemSavePhotoUpload(ServerRequest serverRequest) {
        Mono<Item> mnItem = serverRequest.multipartData().map(multiPart -> {
            FormFieldPart ffpName = (FormFieldPart) multiPart.toSingleValueMap().get("name");
            FormFieldPart ffpPrice = (FormFieldPart) multiPart.toSingleValueMap().get("price");
            FormFieldPart ffpBrandId = (FormFieldPart) multiPart.toSingleValueMap().get("brand.id");
            FormFieldPart ffpBrandName = (FormFieldPart) multiPart.toSingleValueMap().get("brand.name");
            Brand brand = new Brand(ffpBrandId.value(), ffpBrandName.value());
            return new Item(ffpName.value(), Double.parseDouble(ffpPrice.value()), brand);
        });
        return serverRequest.multipartData()
        .map(multiPart -> multiPart.toSingleValueMap().get("filePart"))
        .cast(FilePart.class)
        .flatMap(filePart -> mnItem.flatMap(itemFind -> {
            itemFind.setPhoto(UUID.randomUUID().toString().concat("-")
                    .concat(filePart.filename().replace(" ", "_").replace(":", "").replace("\\", "")));
            return filePart.transferTo(new File(configUploadPath.concat("/").concat(itemFind.getPhoto())))
                    .then(iItemService.save(itemFind));
            }))
        .flatMap(itemSaved -> ServerResponse
            .created(URI.create(DataCommon.COLLECTION_API_ITEM_VS2.concat("/").concat(itemSaved.getId())))
            .contentType(MediaType.APPLICATION_JSON).bodyValue(itemSaved));
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

    public Mono<ServerResponse> itemEdit(ServerRequest serverRequest) {
        Mono<Item> mnItemReq = serverRequest.bodyToMono(Item.class);
        String id = serverRequest.pathVariable("id");
        Mono<Item> mnItemFind = iItemService.findById(id);

        return mnItemFind.zipWith(mnItemReq, (dbItem, reqItem) -> {
            dbItem.setName(reqItem.getName());
            dbItem.setPrice(reqItem.getPrice());
            dbItem.setBrand(reqItem.getBrand());
            return dbItem;
        }).flatMap(item -> ServerResponse
                .created(URI.create(DataCommon.COLLECTION_API_ITEM_VS2.concat("/").concat(item.getId())))
                .contentType(MediaType.APPLICATION_JSON).body(iItemService.save(item), Item.class))
        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> itemDelete(ServerRequest serverRequest) {
        return iItemService.findById(serverRequest.pathVariable("id"))
                .flatMap(item -> iItemService.delete(item).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


}
