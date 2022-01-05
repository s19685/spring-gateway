package pl.szerownia.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static pl.szerownia.gateway.Microservices.*;

@Configuration
public class CloudConfig {


    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder b) {
        return b.routes()

                .route(r -> r.path("/login", "/logout").uri(API_URL))

                .route(r -> r.path("/api/**").uri(API_URL))

                .route(r -> r.path("/msg/**").uri(MESSAGES_URL))

                .build();
    }

    @Bean
    public WebClient http(WebClient.Builder builder) {
        return builder.build();
    }
}

@RestController
class Controller {

    private final CrmClient crmClient;

    public Controller(CrmClient crmClient) {
        this.crmClient = crmClient;
    }

    @PostMapping("/msg/newMessage/{id}")
    public Mono<String> getMessage(@PathVariable String id) {
        return crmClient.sendNewMessage(id);
    }
}


@Component
class CrmClient {

    private final WebClient http;

    public CrmClient(WebClient http) {
        this.http = http;
    }


    Mono<String> sendNewMessage(String id) {
        return http
                .get()
                .uri(API_URL + "user/hasAd" + id)
                .retrieve()
                .onStatus(HttpStatus::isError, returnError())
                .bodyToMono(String.class)
                .then(http
                        .post()
                        .uri(MESSAGES_URL + "newMessage/" + id)
                        .retrieve()
                        .bodyToMono(String.class));
    }

    private Function<ClientResponse, Mono<? extends Throwable>> returnError() {
        return response -> {
            throw new NotFound();
        };
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found.")
    private class NotFound extends RuntimeException {
    }
}
