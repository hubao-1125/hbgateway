package io.github.hubao.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/*
 * Desc: gateway router
 *
 * @author hubao
 * @see 2024/5/21 20:32
 */
@Component
public class GatewayRouter {

    @Autowired
    private HelloHandler helloHandler;

    @Autowired
    private GatewayHandler gatewayHandler;


    @Bean
    public RouterFunction<?> helloRouterFunction() {
        return route(GET("/hello"), helloHandler::handler);
    }

    @Bean
    public RouterFunction<?> gatewayRouterFunction() {
        return route(GET("/gw").or(POST("/gw/**")), gatewayHandler::handler);
    }

}
