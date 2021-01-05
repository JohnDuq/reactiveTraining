package com.udemy.spring.boot.webflux.client.app;

import com.udemy.spring.boot.webflux.client.app.common.Path;
import com.udemy.spring.boot.webflux.client.app.handler.ItemHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {
    
    @Bean
    public RouterFunction<ServerResponse> routes(ItemHandler itemHandler){
        return RouterFunctions
            .route(RequestPredicates.GET(Path.API_CLIENT), itemHandler::findAll)
            .andRoute(RequestPredicates.GET(Path.API_CLIENT.concat(Path.ID)), itemHandler::findById);
    }

}
