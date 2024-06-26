package com.triphippie.apiGateway.configuration;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
                throw new RuntimeException("Missing auth information");

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if(!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Incorrect auth format");
            }
            String jwt = authHeader.substring(7);
            String body = "{ token: " + jwt + " }";
            return webClientBuilder.build()
                    .post()
                    .uri("http://user-service/api/users/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(body))
                    .retrieve().bodyToMono(Boolean.class)
                    .map(aBoolean -> {
                        System.out.println(aBoolean);
                        return exchange;
                    }).flatMap(chain::filter);
        };
    }

    public static class Config {

    }
}
