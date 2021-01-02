package com.udemy.springboot.api.rest.reactor.app;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.udemy.springboot.api.rest.reactor.app.common.DataCommon;
import com.udemy.springboot.api.rest.reactor.app.handler.ItemHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ItemHandler itemHandler) {
        return route(GET(DataCommon.COLLECTION_API_ITEM_VS2).or(GET(DataCommon.COLLECTION_API_ITEM_VS2_OR)),
                itemHandler::itemList);
    }

}
