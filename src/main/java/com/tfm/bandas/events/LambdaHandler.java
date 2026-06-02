package com.tfm.bandas.events;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Punto de entrada de AWS Lambda para MS Events.
 *
 * En local (Docker Compose), Spring Boot arranca mediante 'java -jar app.jar'
 * y el punto de entrada es EventosApplication (@SpringBootApplication).
 *
 * En AWS Lambda, el runtime invoca directamente handleRequest() por cada
 * petición HTTP que llega desde API Gateway HTTP API (payload format v2).
 *
 * El bloque estático inicializa Spring Boot una única vez en el cold start,
 * incluyendo el contexto completo: JPA, Flyway, Feign clients y todas las
 * anotaciones de EventosApplication (@ConfigurationPropertiesScan, @EnableFeignClients).
 * SnapStart toma el snapshot tras esa inicialización completa.
 */
public class LambdaHandler implements RequestHandler<HttpApiV2ProxyRequest, AwsProxyResponse> {

    private static final SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(EventosApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("No se pudo inicializar el contenedor Spring Boot en Lambda", e);
        }
    }

    @Override
    public AwsProxyResponse handleRequest(HttpApiV2ProxyRequest input, Context context) {
        return handler.proxy(input, context);
    }
}