package io.github.hubao.gateway.web.handler;

import io.github.hubao.gateway.DefaultGatewayPluginChain;
import io.github.hubao.gateway.GatewayPlugin;
import io.github.hubao.hbrpc.core.api.LoadBalancer;
import io.github.hubao.hbrpc.core.api.RegistryCenter;
import io.github.hubao.hbrpc.core.cluster.RoundRibonLoadBalancer;
import io.github.hubao.hbrpc.core.meta.InstanceMeta;
import io.github.hubao.hbrpc.core.meta.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/23 20:22
 */
@Component("gatewayWebHandler")
@Slf4j
public class GatewayWebHandler implements WebHandler {


    @Autowired
    List<GatewayPlugin> plugins;



    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {

        log.info("HB Gateway web handler ...");

        if (plugins == null || plugins.isEmpty()) {
            String mock = """
                    {"result":"no plugin"}
                    """;
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
        }

        return new DefaultGatewayPluginChain(plugins).handle(exchange);

//        for (GatewayPlugin plugin : plugins) {
//            if (plugin.support(exchange)) {
//                return plugin.handle(exchange);
//            }
//        }

//        String mock = """
//                    {"result":"no supported plugin"}
//                    """;
//        return exchange.getResponse()
//                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }

}
