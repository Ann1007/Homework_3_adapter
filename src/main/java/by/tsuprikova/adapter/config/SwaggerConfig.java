package by.tsuprikova.adapter.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

////localhost:9090/swagger-ui/index.html
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("ADAPTER SERVICE")
                                .version("1.0.0")
                                .description("The adapter service accepts the request, " +
                                        "then interacts with the second service(smv) and gives a response")
                                .contact(new Contact().name("Ann"))

                );
    }


}
