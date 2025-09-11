package com.tfm.bandas.eventos.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;


@ConfigurationProperties(prefix = "events.rules")
public record EventRulesProperties(
        @DefaultValue("15") @Min(1)  int minDurationMinutes,
        @DefaultValue("240") @Min(1) @Max(8764) int maxDurationHours,
        @DefaultValue("false") boolean allowOverlapSameLocation,
        @DefaultValue("true") boolean allowCreateInPast
) {}
