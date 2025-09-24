package com.tfm.bandas.eventos.dto.mapper;

import com.tfm.bandas.eventos.dto.CalendarEventItemDTO;
import com.tfm.bandas.eventos.dto.EventCreateRequestDTO;
import com.tfm.bandas.eventos.dto.EventResponseDTO;
import com.tfm.bandas.eventos.exception.BadRequestException;
import com.tfm.bandas.eventos.model.entity.EventEntity;
import com.tfm.bandas.eventos.utils.EventStatus;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EventMapper {

  private static final DateTimeFormatter ISO_OFFSET = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  // Validación + conversión LocalDateTime + zone -> Instant
  private static Instant toInstant(LocalDateTime local, String zoneId) {
    ZoneId zone;
    try { zone = ZoneId.of(zoneId); }
    catch (Exception ex) { throw new BadRequestException("Invalid timeZone: " + zoneId); }
    return local.atZone(zone).toInstant();
  }

  public static EventEntity toEntityNew(EventCreateRequestDTO req) {
    Instant start = toInstant(req.localStart(), req.timeZone());
    Instant end   = toInstant(req.localEnd(), req.timeZone());
    if (!end.isAfter(start)) throw new BadRequestException("end must be after start");

    return EventEntity.builder()
        .id(UUID.randomUUID().toString())
        .title(req.title())
        .description(req.description())
        .location(req.location())
        .type(req.type())
        .status(req.status() == null ? EventStatus.SCHEDULED : req.status())
        .visibility(req.visibility())
        .timeZone(req.timeZone())
        .startAt(start)
        .endAt(end)
        .build();
  }

  public static void copyToEntityUpdate(EventCreateRequestDTO req, EventEntity e) {
    Instant start = toInstant(req.localStart(), req.timeZone());
    Instant end   = toInstant(req.localEnd(), req.timeZone());
    if (!end.isAfter(start)) throw new BadRequestException("end must be after start");

    e.setTitle(req.title());
    e.setDescription(req.description());
    e.setLocation(req.location());
    e.setType(req.type());
    e.setStatus(req.status());
    e.setVisibility(req.visibility());
    e.setTimeZone(req.timeZone());
    e.setStartAt(start);
    e.setEndAt(end);
  }

  public static EventResponseDTO toResponse(EventEntity e) {
    ZoneId zone = ZoneId.of(e.getTimeZone());
    String startLocal = e.getStartAt().atZone(zone).toOffsetDateTime().format(ISO_OFFSET);
    String endLocal   = e.getEndAt().atZone(zone).toOffsetDateTime().format(ISO_OFFSET);

    return new EventResponseDTO(
        e.getId(),
        e.getVersion(),
        e.getTitle(),
        e.getDescription(),
        e.getLocation(),
        e.getType(),
        e.getStatus(),
        e.getVisibility(),
        e.getTimeZone(),
        e.getStartAt(),
        e.getEndAt(),
        startLocal,
        endLocal
    );
  }

  public static CalendarEventItemDTO toCalendarItem(EventEntity e, ZoneId tzOpt) {
    ZoneId zone = (tzOpt != null) ? tzOpt : ZoneId.of(e.getTimeZone());
    String start = e.getStartAt().atZone(zone).toOffsetDateTime().format(ISO_OFFSET);
    String end   = e.getEndAt().atZone(zone).toOffsetDateTime().format(ISO_OFFSET);
    return new CalendarEventItemDTO(
        e.getId(), e.getTitle(), start, end, false,
        e.getType(), e.getStatus(), zone.getId(), e.getLocation()
    );
  }
}
