package com.udemy.spring.boot.webflux.client.app;

import com.udemy.spring.boot.webflux.client.app.common.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${config.base.endPoint}")
    private String configBaseEndPoint;

    @Bean
    @LoadBalanced
    public WebClient.Builder buildWebClient(){
        return WebClient.builder().baseUrl(configBaseEndPoint.concat(Path.API_ITEM));
    }

}
