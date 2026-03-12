package com.tfm.bandas.events.client;

import com.tfm.bandas.events.config.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para comunicación con MS Surveys
 */
@FeignClient(
        name = "surveys-client",
        url = "${surveys.service.uri}",
        configuration = FeignSecurityConfig.class
)
public interface SurveysFeignClient {
    /**
     * Solicita a MS Surveys que elimine todas las encuestas asociadas a un evento.
     * Invocado únicamente desde deleteEvent como parte de la cascada de borrado.
     */
    @DeleteMapping("${surveys.service.deleteSurveysByEventIdPath}")
    void deleteSurveysByEventId(@PathVariable("eventId") String eventId);
}

