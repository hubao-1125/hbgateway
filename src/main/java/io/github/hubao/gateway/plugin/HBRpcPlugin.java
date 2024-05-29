package io.github.hubao.gateway.plugin;

import io.github.hubao.gateway.AbstractGatewayPlugin;
import io.github.hubao.gateway.GatewayPluginChain;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/29 20:22
 */
@Component("hbrpc")
@Slf4j
public class HBRpcPlugin extends AbstractGatewayPlugin {

    @Autowired
    private RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RoundRibonLoadBalancer<>();

    public static final String NAME = "hbrpc";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {

        log.info("hb gatewayWebHandler handle");

        String service = exchange.getRequest().getPath().value().substring(4);
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app("app1").env("dev").namespace("public").name(service)
                .build();
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);
        System.out.println(" inst size=" + instanceMetas.size() + ", inst  " + instanceMeta);
        String url = instanceMeta.toUrl();

        // 4. 拿到请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        // 6. 通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);

        // 7. 组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("hb.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("hb.gw.plugin", getName());

        return body.flatMap(x -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                .then(chain.handle(exchange));

    }

    @Override
    public String getName() {
        return NAME;
    }
}
