package by.tsuprikova.adapter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.WebClient;


@Getter
@Setter
@Configuration
public class WebClientConfig {

    @Value("${smv.base.url}")
    private String baseUrl;


    @Bean
    public WebClient init() {
        return WebClient.builder().baseUrl(baseUrl).
                build();

    }


}
