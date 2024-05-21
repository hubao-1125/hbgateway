package io.github.hubao.gateway;

import io.github.hubao.hbrpc.core.api.LoadBalancer;
import io.github.hubao.hbrpc.core.api.RegistryCenter;
import io.github.hubao.hbrpc.core.cluster.RoundRibonLoadBalancer;
import io.github.hubao.hbrpc.core.meta.InstanceMeta;
import io.github.hubao.hbrpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/21 21:07
 */
@Component
public class GatewayHandler {

    @Autowired
    private RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RoundRibonLoadBalancer<>();

    Mono<ServerResponse> handler(ServerRequest request) {

        String service = request.path().substring(4);
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app("app1").env("dev").namespace("public").name(service)
                .build();
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);
        System.out.println(" inst size=" + instanceMetas.size() + ", inst  " + instanceMeta);
        String url = instanceMeta.toUrl();

        // 4. 拿到请求的报文
        Mono<String> requestMono = request.bodyToMono(String.class);
        return requestMono.flatMap(x -> invokeFromRegistry(x, url));
    }

    private Mono<? extends ServerResponse> invokeFromRegistry(String x, String url) {
        // 5. 通过webclient发送post请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(x).retrieve().toEntity(String.class);
        // 6. 通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        body.subscribe(souce -> System.out.println("response:" + souce));
        // 7. 组装响应报文
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("hb.gw.version", "v1.0.0")
                .body(body, String.class);
    }
}