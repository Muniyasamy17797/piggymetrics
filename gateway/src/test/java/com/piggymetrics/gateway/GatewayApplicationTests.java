package com.piggymetrics.gateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GatewayApplicationTests {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testRouteConfiguration() {
        StepVerifier.create(routeLocator.getRoutes())
                .expectNextCount(2)  // We expect 2 routes (accounts and statistics)
                .verifyComplete();
    }
}