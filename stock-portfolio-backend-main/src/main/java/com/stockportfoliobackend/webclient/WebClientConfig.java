package com.stockportfoliobackend.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
public class WebClientConfig {

    @Value("${alpha.vantage.base-url}")
    private String apiurl;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(apiurl).build();
    }
}
