package com.udemy.springboot.webflux.app;

import java.util.Date;

import com.udemy.springboot.webflux.app.common.DataCommon;
import com.udemy.springboot.webflux.app.model.documents.Item;
import com.udemy.springboot.webflux.app.model.documents.Brand;
import com.udemy.springboot.webflux.app.service.IItemService;
import com.udemy.springboot.webflux.app.service.IBrandService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	@Autowired
	private IItemService iItemService;
	@Autowired
	private IBrandService iBrandService;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		reactiveMongoTemplate.dropCollection(DataCommon.COLLECTION_ITEM).subscribe();
		reactiveMongoTemplate.dropCollection(DataCommon.COLLECTION_BRAND).subscribe();

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
