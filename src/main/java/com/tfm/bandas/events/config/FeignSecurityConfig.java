package com.tfm.bandas.events.config;

import feign.codec.Decoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuración para clientes Feign:
 *
 * 1. Propaga el JWT de la petición entrante a las peticiones salientes hacia
 *    MS Surveys a través del API Gateway. Sin esto, el Gateway rechaza
 *    la llamada con 401.
 *
 * 2. Decoder personalizado que acepta text/plain como JSON.
 *    El contenedor serverless establece Content-Type: text/plain en las
 *    respuestas Lambda aunque el body sea JSON válido. El decoder estándar
 *    de Feign falla porque MappingJackson2HttpMessageConverter solo acepta
 *    application/json. Este decoder registra Jackson con soporte adicional
 *    para text/plain, permitiendo deserializar la respuesta correctamente.
 *    Aunque la llamada actual (DELETE void) no deserializa respuesta, el
 *    patrón se aplica por coherencia y para cubrir futuras extensiones.
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

    @Bean
    public Decoder feignDecoder() {
        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes =
                new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        jacksonConverter.setSupportedMediaTypes(supportedMediaTypes);
        return new SpringDecoder(
                () -> new HttpMessageConverters(
                        false, Collections.singletonList(jacksonConverter))
        );
    }
}
