package com.tfm.bandas.eventos.dto;

import com.tfm.bandas.eventos.utils.EventStatus;
import com.tfm.bandas.eventos.utils.EventType;
import com.tfm.bandas.eventos.utils.EventVisibility;

import java.time.Instant;

public record EventResponse(
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
