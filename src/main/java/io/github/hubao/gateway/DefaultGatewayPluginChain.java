package io.github.hubao.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/29 21:07
 */
public class DefaultGatewayPluginChain implements GatewayPluginChain {


    List<GatewayPlugin> plugins;
    int index = 0;


    public DefaultGatewayPluginChain(List<GatewayPlugin> plugins) {
        this.plugins = plugins;
    }




    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {

        return Mono.defer(() -> {
            if (index < plugins.size()) {
                return plugins.get(index++).handle(exchange, this);
            }
            return Mono.empty();
        });
    }
}
