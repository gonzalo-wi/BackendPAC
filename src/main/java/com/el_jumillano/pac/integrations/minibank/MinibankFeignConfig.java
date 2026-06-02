package com.el_jumillano.pac.integrations.minibank;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Configuración Feign para Minibank: inyecta Basic Auth en cada petición.
 * Sin @Configuration para que el interceptor sea local a este FeignClient
 * y no se aplique globalmente a todos los clientes Feign.
 */
public class MinibankFeignConfig {

    @Value("${pac.integrations.minibank.username:}")
    private String username;

    @Value("${pac.integrations.minibank.password:}")
    private String password;

    @Bean
    public RequestInterceptor minibankBasicAuthInterceptor() {
        return template -> {
            String credentials = username + ":" + password;
            String encoded = Base64.getEncoder().encodeToString(
                    credentials.getBytes(StandardCharsets.UTF_8));
            template.header("Authorization", "Basic " + encoded);
        };
    }
}
