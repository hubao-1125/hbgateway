package io.github.hubao.gateway;


import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/29 20:13
 */
public interface GatewayPlugin {

    String GATEWAY_PREFIX = "/gw";

    void start();
    void stop();
    String getName();

    boolean support(ServerWebExchange exchange);

    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);
}
