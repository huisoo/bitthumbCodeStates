package example.ReactorPractice;

import example.ReactorPractice.PostHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.awt.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@EnableWebFlux
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> router(CustomHandler customHandler){
        return RouterFunctions.route()
                .GET("/hello", customHandler::someRequestMethod)
                .build();
    }
}
