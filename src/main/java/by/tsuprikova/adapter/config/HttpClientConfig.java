package by.tsuprikova.adapter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Getter
@Setter
@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient initClient(){
        return HttpClient.newHttpClient();
    }

}
