package io.github.hubao.gateway.plugin;

import io.github.hubao.gateway.AbstractGatewayPlugin;
import io.github.hubao.gateway.GatewayPluginChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/29 20:27
 */
@Component("direct")
@Slf4j
public class DirectPlugin extends AbstractGatewayPlugin {


    public static final String NAME = "direct";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return false;
    }

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {

        log.info("direct plugin in ================>");
        String backend = exchange.getRequest().getQueryParams().getFirst("backend");

        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("hb.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("hb.gw.plugin", getName());

        if (backend == null || backend.isEmpty()) {
            return requestBody.flatMap(x -> exchange.getResponse().writeWith(Mono.just(x))).then(chain.handle(exchange));
        }

        WebClient client = WebClient.create(backend);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        // 6. 通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);

        return body.flatMap(x -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                .then(chain.handle(exchange));
    }

    @Override
    public String getName() {
        return "";
    }
}
