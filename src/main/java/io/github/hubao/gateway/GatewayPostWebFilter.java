package io.github.hubao.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/23 21:29
 */
@Component
@Slf4j
public class GatewayPostWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return chain.filter(exchange).doFinally(x->{

            log.info("post filter");
            exchange.getAttributes().forEach((k, v) -> {
                log.info("key:{} value:{}", k, v);
            });
        });
    }
}
