package com.udemy.springboot.webflux.app.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import com.udemy.springboot.webflux.app.model.documents.Brand;
import com.udemy.springboot.webflux.app.model.documents.Item;
import com.udemy.springboot.webflux.app.service.IBrandService;
import com.udemy.springboot.webflux.app.service.IItemService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SessionAttributes("item")
@Controller
public class ItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    @Value("${config.upload.path}")
    private String configUploadPath;
    @Autowired
    private IItemService iItemService;
    @Autowired
    private IBrandService iBrandService;

    @ModelAttribute("brands")
    public Flux<Brand> brands() {
        return iBrandService.findAll();
    }

    @GetMapping("/uploads/img/{photoName:.+}")
    public Mono<ResponseEntity<Resource>> getPhoto(@PathVariable String photoName) throws MalformedURLException {
        Path path = Paths.get(configUploadPath).resolve(photoName).toAbsolutePath();
        Resource resource = new UrlResource(path.toUri());
        return Mono.just(
            ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename())
            .body(resource)
        );
    }


    @GetMapping("/detail/{id}")
    public Mono<String> detail(Model model,@PathVariable String id){
        return iItemService.findById(id)
        .doOnNext(item -> {
            model.addAttribute("item", item);
            model.addAttribute("title", "Item Detail");
        })
        .switchIfEmpty(Mono.just(new Item()))
        .flatMap(item -> {
            if (item == null || item.getId() == null) {
                return Mono.error(new Exception("Item dont exist"));
            } else {
                return Mono.just(item);
            }
        }).then(Mono.just("detail"))
        .onErrorResume(error -> {
            LOGGER.error(error.getMessage(), error);
            return Mono.just("redirect:/item-list?error=" + error.getMessage().replace(" ", "+"));
        });
    }

    @GetMapping({ "/item-list", "/" })
    public Mono<String> list(Model model) {
        Flux<Item> flxItem = iItemService.findAllNameUpper();
        flxItem.subscribe(item -> LOGGER.info(item.toString()));
        model.addAttribute("items", flxItem);
        model.addAttribute("title", "Item List");
        return Mono.just("item-list");
    }

    @GetMapping("/form")
    public Mono<String> form(Model model) {
        model.addAttribute("title", "Item Form");
        model.addAttribute("item", new Item());
        model.addAttribute("btn", "Save");
        return Mono.just("form");
    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> edit2(@PathVariable("id") String id, Model model) {
        return iItemService.findById(id).doOnNext(item -> {
            LOGGER.info(item.toString());
            model.addAttribute("title", "Edit Item Form");
            model.addAttribute("item", item);
            model.addAttribute("btn", "Edit");
        }).defaultIfEmpty(new Item()).flatMap(item -> {
            if (item == null || item.getId() == null) {
                return Mono.error(new Exception("Item dont exist"));
            } else {
                return Mono.just(item);
            }
        }).thenReturn("form").onErrorResume(Exception.class, error -> {
            LOGGER.error(error.getMessage(), error);
            return Mono.just("redirect:/item-list?error=" + error.getMessage().replace(" ", "+"));
        });

    }

    @GetMapping("/form/{id}")
    public Mono<String> edit(@PathVariable("id") String id, Model model) {
        Mono<Item> mnItem = iItemService.findById(id).defaultIfEmpty(new Item());
        mnItem.doOnNext(item -> {
            LOGGER.info(item.toString());
        });
        model.addAttribute("title", "Edit Item Form");
        model.addAttribute("item", mnItem);
        model.addAttribute("btn", "Save");
        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> save(@Valid Item item, BindingResult bindingResult,
            @RequestPart(name = "file") FilePart filePart, SessionStatus sessionStatus, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Error Item Form");
            model.addAttribute("btn", "Save");
            return Mono.just("form");
        } else {
            sessionStatus.setComplete();
            Mono<Brand> mnBrand = iBrandService.findById(item.getBrand().getId());

            return mnBrand.flatMap(brand -> {
                if (item.getCreateAt() == null) {
                    item.setCreateAt(new Date());
                }
                if (!filePart.filename().isEmpty()) {
                    item.setPhoto(UUID.randomUUID().toString().concat("_")
                            .concat(filePart.filename().replace(" ", "_").replace(":", "").replace("\\", "")));
                }
                item.setBrand(brand);
                return iItemService.save(item);
            }).doOnNext(itemLog -> {
                LOGGER.info(itemLog.toString());
            }).flatMap(itemSaved -> {
                if (!filePart.filename().isEmpty()) {
                    return filePart
                            .transferTo(new File(configUploadPath.concat("//").concat(itemSaved.getPhoto())));
                }
                return Mono.empty();
            }).thenReturn("redirect:/item-list?success=Created+successfully");
        }
    }

    @GetMapping("/delete/{id}")
    public Mono<String> delete(@PathVariable String id) {
        return iItemService.findById(id).defaultIfEmpty(new Item()).flatMap(item -> {
            if (item == null || item.getId() == null) {
                return Mono.error(new Exception("Item dont exist"));
            } else {
                LOGGER.info("Item delete", item.toString());
                return iItemService.delete(item);
            }
        }).then(Mono.just("redirect:/item-list?success=Deleted+successfully")).onErrorResume(Exception.class, error -> {
            LOGGER.error(error.getMessage(), error);
            return Mono.just("redirect:/item-list?error=" + error.getMessage().replaceAll(" ", "+"));
        });
    }

    @GetMapping("/item-list-datadriver")
    public String reactiveDataDriver(Model model) {
        Flux<Item> flxItem = iItemService.findAllNameUpper().delayElements(Duration.ofSeconds(1));
        flxItem.subscribe(item -> LOGGER.info(item.toString()));
        model.addAttribute("items", new ReactiveDataDriverContextVariable(flxItem, 1));
        model.addAttribute("title", "Item List");
        return "item-list";
    }

    @GetMapping("/item-list-chuncked")
    public String chuncked(Model model) {
        Flux<Item> flxItem = iItemService.findAllNameUpperRepeat();
        model.addAttribute("items", flxItem);
        model.addAttribute("title", "Item List");
        return "item-list";
    }

    @GetMapping("/item-list-chuncked-2")
    public String chuncked2(Model model) {
        Flux<Item> flxItem = iItemService.findAllNameUpperRepeat();
        model.addAttribute("items", flxItem);
        model.addAttribute("title", "Item List");
        return "/chuncked/item-list-chuncked";
    }

}
