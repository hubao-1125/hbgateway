package io.github.hubao.gateway;

import io.github.hubao.hbrpc.core.api.RegistryCenter;
import io.github.hubao.hbrpc.core.registry.hb.HbRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Desc:
 *
 * @author hubao
 * @see 2024/5/21 21:10
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter rc() {
        return new HbRegistryCenter();
    }
}
