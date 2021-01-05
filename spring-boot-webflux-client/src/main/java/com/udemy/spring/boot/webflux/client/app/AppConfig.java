package com.udemy.spring.boot.webflux.client.app;

import com.udemy.spring.boot.webflux.client.app.common.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient buiWebClient(){
        return WebClient.create(Path.END_POINT.concat(Path.API_ITEM));
    }

}
