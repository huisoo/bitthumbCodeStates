package example.ReactorPractice;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CustomHandler {

    public Mono<ServerResponse> someRequestMethod(ServerRequest request){

        String name = request.queryParam("name").get();
        Info info = new Info(name, "hello " + name);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(info);
    }
}
