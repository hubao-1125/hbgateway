package io.github.hubao.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/29 20:15
 */
public abstract class AbstractGatewayPlugin implements GatewayPlugin{
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return doSupport(exchange);
    }

    public abstract boolean doSupport(ServerWebExchange exchange);

    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain){
        boolean supported = doSupport(exchange);
        System.out.println(" ====> plugin[" + this.getName() + "], support=" + supported);
        return supported ? doHandle(exchange, chain) : chain.handle(exchange);
    }
}
