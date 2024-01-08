package com.bills.billsapigateway;

import org.apache.http.impl.io.SocketOutputBuffer;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class InnerFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
        System.out.println("UserAgent: "+userAgent);
        if(!Pattern.matches(".*Java.*",userAgent))
        {
            System.out.println("This service is only for inner requests");
            return this.onError(exchange, HttpStatus.BAD_REQUEST);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
