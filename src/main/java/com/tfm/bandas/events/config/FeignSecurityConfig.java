package com.tfm.bandas.events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Configuración Feign para propagar el token JWT del contexto de seguridad actual
 * a las llamadas salientes hacia otros microservicios a través del Gateway.
 * El Gateway requiere autenticación en todos sus endpoints. Sin esta configuración,
 * las llamadas Feign de Events hacia Surveys (vía Gateway) serían rechazadas con 401.
 */
@Configuration
public class FeignSecurityConfig {

    @Bean
    public feign.RequestInterceptor oauth2FeignRequestInterceptor() {
        return template -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                String token = jwtAuth.getToken().getTokenValue();
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        };
    }
}
