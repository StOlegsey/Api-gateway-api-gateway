package com.bills.billsapigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    private final InnerFilter innerFilter;

    public GatewayConfig(InnerFilter innerFilter) {
        this.innerFilter = innerFilter;
    }

    @Bean
    public RouteLocator mainRouteLocator(RouteLocatorBuilder builder) {
        // adding 2 rotes to first microservice as we need to log request body if method is POST
        return builder.routes()
                .route("bills-api",r -> r.path("/bills-api/api/genTickets")
                        .and().method("POST")
                        .filters(f -> f.rewritePath("/bills-api/api/genTickets", "/api/genTickets"))
                        .uri("lb://bills-api"))
                .route("bills-api",r -> r.path("/bills-api/test")
                        .and().method("GET")
                        .filters(f-> f.rewritePath("/bills-api/test","/test"))
                        .uri("lb://bills-api"))
                .route("ticket-generator",r -> r.path("/ticket-generator/gen")
                        .and().method("GET")
                        .filters(f-> f.rewritePath("/ticket-generator/gen","/gen").filter(innerFilter))
                        .uri("lb://ticket-generator"))
                .build();
    }
}
