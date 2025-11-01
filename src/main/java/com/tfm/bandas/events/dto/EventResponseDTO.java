package com.tfm.bandas.events.dto;

import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.tfm.bandas.events.utils.EventVisibility;

import java.time.Instant;

public record EventResponseDTO(
    String id,
    int version,
    String title,
    String description,
    String location,
    EventType type,
    EventStatus status,
    EventVisibility visibility,
    String timeZone,
    Instant startAt, // UTC
    Instant endAt,   // UTC
    String startLocal, // ISO con offset, calculado con timeZone
    String endLocal
) {}
