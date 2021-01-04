package com.udemy.springboot.api.rest.reactor.app;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.udemy.springboot.api.rest.reactor.app.common.Path;
import com.udemy.springboot.api.rest.reactor.app.handler.ItemHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ItemHandler itemHandler) {
        return route(
                GET(Path.API_V2_ITEM).or(GET(Path.API_V2_OR_ITEM)), itemHandler::itemList)
                .andRoute(GET(Path.API_V2_ITEM.concat(Path.ID)) //.and(contentType(MediaType.APPLICATION_JSON)) 
                        ,itemHandler::itemDetail)
                .andRoute(POST(Path.API_V2_ITEM), itemHandler::itemSave)
                .andRoute(PUT(Path.API_V2_ITEM.concat(Path.ID)), itemHandler::itemEdit)
                .andRoute(DELETE(Path.API_V2_ITEM.concat(Path.ID)), itemHandler::itemDelete)
                .andRoute(POST(Path.API_V2_ITEM.concat(Path.UPLOAD_ID)), itemHandler::itemPhotoUpload)
                .andRoute(POST(Path.API_V2_ITEM.concat(Path.FUNC).concat(Path.UPLOAD_V2)), 
                        itemHandler::itemSavePhotoUpload);
    }

}
