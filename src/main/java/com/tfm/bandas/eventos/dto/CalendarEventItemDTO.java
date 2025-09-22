package com.tfm.bandas.eventos.dto;

import com.tfm.bandas.eventos.utils.EventStatus;
import com.tfm.bandas.eventos.utils.EventType;

public record CalendarEventItemDTO(
    String id,
    String title,
    String start,      // ISO-8601 con offset: 2025-10-20T19:00:00+02:00
    String end,        // idem
    boolean allDay,    // por si algún día hay eventos de día completo
    EventType type,
    EventStatus status,
    String timeZone,   // "Europe/Madrid" (útil si el front quiere mostrar zona)
    String location
) {}
