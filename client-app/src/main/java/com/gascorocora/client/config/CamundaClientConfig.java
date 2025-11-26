package com.gascorocora.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CamundaClientConfig {

    @Value("${camunda.rest.url}")
    private String camundaRestUrl;

    @Bean
    public WebClient camundaWebClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(camundaRestUrl)
                .exchangeStrategies(strategies)
                .build();
    }
}
