package com.udemy.springboot.api.rest.reactor.app;

import java.util.Date;

import com.udemy.springboot.api.rest.reactor.app.common.DocumentCollection;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Brand;
import com.udemy.springboot.api.rest.reactor.app.model.documents.Item;
import com.udemy.springboot.api.rest.reactor.app.service.IBrandService;
import com.udemy.springboot.api.rest.reactor.app.service.IItemService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Flux;

@EnableEurekaClient
@SpringBootApplication
public class SpringApiReactorApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringApiReactorApplication.class);
	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	@Autowired
	private IItemService iItemService;
	@Autowired
	private IBrandService iBrandService;

	public static void main(String[] args) {
		SpringApplication.run(SpringApiReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		reactiveMongoTemplate.dropCollection(DocumentCollection.COLLECTION_ITEM).subscribe();
		reactiveMongoTemplate.dropCollection(DocumentCollection.COLLECTION_BRAND).subscribe();

		Brand sonyType = new Brand("SONY");
		Brand microsoftType = new Brand("MICROSOFT");

		Flux.just(sonyType,microsoftType)
		.flatMap(iBrandService::save)
		.doOnNext(typeSaved -> LOGGER.info(typeSaved.toString()))
		.thenMany(
			Flux.just(
				new Item("PLAYSTATION 5", 598d, sonyType),
				new Item("XBOX SERIES X", 599d, microsoftType),
				new Item("PLAYSTATION 4", 200d, sonyType),
				new Item("XBOX 360", 201d, microsoftType)
			)
			.flatMap(itemFlattened -> {
				itemFlattened.setCreateAt(new Date());
				return iItemService.save(itemFlattened);})
		).subscribe(itemSaved -> LOGGER.info(itemSaved.toString()));
	}

}
