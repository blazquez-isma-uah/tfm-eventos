package com.tfm.bandas.eventos.dto.mapper;

import com.tfm.bandas.eventos.dto.CalendarEventItem;
import com.tfm.bandas.eventos.dto.EventCreateRequest;
import com.tfm.bandas.eventos.dto.EventResponse;
import com.tfm.bandas.eventos.dto.EventUpdateRequest;
import com.tfm.bandas.eventos.exception.BadRequestException;
import com.tfm.bandas.eventos.model.entity.Event;
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

  public static Event toEntityNew(EventCreateRequest req) {
    Instant start = toInstant(req.localStart(), req.timeZone());
    Instant end   = toInstant(req.localEnd(), req.timeZone());
    if (!end.isAfter(start)) throw new BadRequestException("end must be after start");

    return Event.builder()
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

  public static void copyToEntityUpdate(EventUpdateRequest req, Event e) {
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

  public static EventResponse toResponse(Event e) {
    ZoneId zone = ZoneId.of(e.getTimeZone());
    String startLocal = e.getStartAt().atZone(zone).toOffsetDateTime().format(ISO_OFFSET);
    String endLocal   = e.getEndAt().atZone(zone).toOffsetDateTime().format(ISO_OFFSET);

    return new EventResponse(
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

  public static CalendarEventItem toCalendarItem(Event e, ZoneId tzOpt) {
    ZoneId zone = (tzOpt != null) ? tzOpt : ZoneId.of(e.getTimeZone());
    String start = e.getStartAt().atZone(zone).toOffsetDateTime().format(ISO_OFFSET);
    String end   = e.getEndAt().atZone(zone).toOffsetDateTime().format(ISO_OFFSET);
    return new CalendarEventItem(
        e.getId(), e.getTitle(), start, end, false,
        e.getType(), e.getStatus(), zone.getId(), e.getLocation()
    );
  }
}
