package com.tfm.bandas.events.dto;

import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.tfm.bandas.events.utils.EventVisibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record EventDTO(
    @JsonProperty("id") String id,
    @JsonProperty("version") int version,
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("location") String location,
    @JsonProperty("type") EventType type,
    @JsonProperty("status") EventStatus status,
    @JsonProperty("visibility") EventVisibility visibility,
    @JsonProperty("startAt") Instant startAt,
    @JsonProperty("endAt") Instant endAt
) {}
