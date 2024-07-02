package com.triphippie.apiGateway.configuration;

import com.triphippie.apiGateway.exception.AuthException;
import com.triphippie.apiGateway.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing auth information");

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if(!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Authorization format");
            }
            String jwt = authHeader.substring(7);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", jwt);
            return webClientBuilder.build()
                    .post()
                    .uri("http://user-service/api/users/validateToken")
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            response -> switch (response.statusCode().value()) {
                                case 401 -> Mono.error(new AuthException(HttpStatus.UNAUTHORIZED));
                                case 403 -> Mono.error(new AuthException(HttpStatus.FORBIDDEN));
                                default -> Mono.error(new AuthException(HttpStatus.INTERNAL_SERVER_ERROR));
                            }
                    )
                    .bodyToMono(UserDto.class)
                    .map(user -> {
                        exchange.getRequest()
                                .mutate()
                                .header("auth-user-id", user.id().toString())
                                .header("auth-user-role", user.role());
                        System.out.println(user);
                        return exchange;
                    }).flatMap(chain::filter);
        };
    }

    public static class Config {

    }
}
