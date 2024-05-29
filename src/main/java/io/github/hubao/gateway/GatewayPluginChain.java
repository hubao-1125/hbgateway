package io.github.hubao.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/29 21:05
 */
public interface GatewayPluginChain {

    Mono<Void> handle(ServerWebExchange exchange);


}
