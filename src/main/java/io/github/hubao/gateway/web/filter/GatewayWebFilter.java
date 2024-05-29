package io.github.hubao.gateway.web.filter;

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
 * @see 2024/5/23 21:23
 */
@Component
@Slf4j
public class GatewayWebFilter implements WebFilter {



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        log.info("hb gateway web filter ~~~");
        String mock = exchange.getRequest().getQueryParams().getFirst("mock");
        if (mock == null) {
            return chain.filter(exchange);
        }

        String mockStr = """
                {"result":"mock"}
                """;
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mockStr.getBytes())));
    }

}
