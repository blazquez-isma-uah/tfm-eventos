package com.tfm.bandas.events.dto;

import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record CalendarEventItemDTO(
    @JsonProperty("id") String id,
    @JsonProperty("title") String title,
    @JsonProperty("start") Instant start,
    @JsonProperty("end") Instant end,
    @JsonProperty("allDay") boolean allDay,
    @JsonProperty("type") EventType type,
    @JsonProperty("status") EventStatus status,
    @JsonProperty("location") String location
) {}
