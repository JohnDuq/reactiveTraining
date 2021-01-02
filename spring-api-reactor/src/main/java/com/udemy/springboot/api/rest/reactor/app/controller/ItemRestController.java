package com.udemy.springboot.api.rest.reactor.app.controller;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import com.udemy.springboot.api.rest.reactor.app.common.DataCommon;
import com.udemy.springboot.api.rest.reactor.app.controller.data.ErrorBadRequestResponse;
import com.udemy.springboot.api.rest.reactor.app.controller.data.ErrorResponse;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;
import com.udemy.springboot.api.rest.reactor.app.service.IItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(DataCommon.COLLECTION_API_ITEM)
public class ItemRestController {

    @Autowired
    private IItemService iItemService;
    @Value("${config.upload.path}")
    private String configUploadPath;

    @GetMapping
    public Mono<ResponseEntity<Flux<Item>>> getItems() {
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(iItemService.findAll()));
    }

    @GetMapping(DataCommon.ID)
    public Mono<ResponseEntity<Item>> getItem(@PathVariable String id) {
        return iItemService.findById(id)
                .map(item -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(item))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Item>> postItem(@RequestBody Item item) {
        if (item.getCreateAt() == null) {
            item.setCreateAt(new Date());
        }
        return iItemService.save(item).map(itemSaved -> ResponseEntity
                .created(URI.create(DataCommon.COLLECTION_API_ITEM.concat(DataCommon.SLASH).concat(itemSaved.getId())))
                .contentType(MediaType.APPLICATION_JSON).body(itemSaved));
    }

    @PostMapping(DataCommon.POST_ITEM_VALID)
    public Mono<ResponseEntity<Map<String, Object>>> postItemValid(@Valid @RequestBody Mono<Item> mnItem) {
        Map<String, Object> response = new HashMap<>();
        return mnItem.map(item -> {
            if (item.getCreateAt() == null)
                item.setCreateAt(new Date());
            return item;
        }).flatMap(iItemService::save).map(itemSaved -> {
            response.put("item", itemSaved);
            return ResponseEntity
                    .created(URI
                            .create(DataCommon.COLLECTION_API_ITEM.concat(DataCommon.SLASH).concat(itemSaved.getId())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }).onErrorResume(error -> Mono.just(error).cast(WebExchangeBindException.class)
                .flatMap(exception -> Mono.just(exception.getFieldErrors())).flatMapMany(Flux::fromIterable)
                .map(fieldError -> "Field name:" + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList().flatMap(list -> {
                    response.put("errors", list);
                    return Mono.just(ResponseEntity.badRequest().body(response));
                }));
    }

    @PostMapping(DataCommon.POST_ITEM_VALID_V2)
    public Mono<ResponseEntity<Item>> postItemValidV2(@Valid @RequestBody Mono<Item> mnItem) {
        return mnItem.map(item -> {
            if (item.getCreateAt() == null)
                item.setCreateAt(new Date());
            return item;
        }).flatMap(iItemService::save).map(itemSaved -> ResponseEntity
                .created(URI.create(DataCommon.COLLECTION_API_ITEM.concat(DataCommon.SLASH).concat(itemSaved.getId())))
                .contentType(MediaType.APPLICATION_JSON).body(itemSaved));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<ErrorResponse>> handleExceptionValidV2(WebExchangeBindException ex) {
        return Mono.just(ex).flatMap(exception -> Mono.just(exception.getFieldErrors())).flatMapMany(Flux::fromIterable)
                .map(fieldError -> new ErrorBadRequestResponse(fieldError, HttpStatus.BAD_REQUEST)).collectList()
                .flatMap(list -> Mono.just(
                        ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), list))));
    }

    @PostMapping(DataCommon.POST_ITEM_V2)
    public Mono<ResponseEntity<Item>> postItemPhoto(Item item, @RequestPart FilePart filePart) {
        if (item.getCreateAt() == null) {
            item.setCreateAt(new Date());
        }
        item.setPhoto(UUID.randomUUID().toString().concat("-")
                .concat(filePart.filename().replace(" ", "_").replace(":", "").replace("\\", "")));
        return filePart.transferTo(new File(configUploadPath.concat("/").concat(item.getPhoto())))
                .then(iItemService.save(item))
                .map(itemSaved -> ResponseEntity
                        .created(URI.create(
                                DataCommon.COLLECTION_API_ITEM.concat(DataCommon.SLASH).concat(itemSaved.getId())))
                        .contentType(MediaType.APPLICATION_JSON).body(itemSaved))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(DataCommon.UPLOAD_ID)
    public Mono<ResponseEntity<Item>> uploadItem(@PathVariable String id, @RequestPart FilePart filePart) {
        return iItemService.findById(id).flatMap(itemFind -> {

            itemFind.setPhoto(UUID.randomUUID().toString().concat("-")
                    .concat(filePart.filename().replace(" ", "_").replace(":", "").replace("\\", "")));

            return filePart.transferTo(new File(configUploadPath.concat("/").concat(itemFind.getPhoto())))
                    .then(iItemService.save(itemFind));
        }).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping(DataCommon.ID)
    public Mono<ResponseEntity<Item>> putItem(@PathVariable String id, @RequestBody Item item) {
        return iItemService.findById(id).flatMap(itemFind -> {
            itemFind.setName(item.getName());
            itemFind.setPrice(item.getPrice());
            itemFind.setBrand(item.getBrand());
            return iItemService.save(itemFind);
        }).map(itemSaved -> ResponseEntity
                .created(URI.create(DataCommon.COLLECTION_API_ITEM.concat(DataCommon.SLASH).concat(itemSaved.getId())))
                .contentType(MediaType.APPLICATION_JSON).body(itemSaved))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping(DataCommon.ID)
    public Mono<ResponseEntity<Object>> deleteItem(@PathVariable String id) {
        return iItemService.findById(id).flatMap(
                itemFind -> iItemService.delete(itemFind).then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}