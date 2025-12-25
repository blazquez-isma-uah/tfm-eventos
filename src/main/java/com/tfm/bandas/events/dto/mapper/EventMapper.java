package com.tfm.bandas.events.dto.mapper;

import com.tfm.bandas.events.dto.CalendarEventItemDTO;
import com.tfm.bandas.events.dto.EventCreateRequestDTO;
import com.tfm.bandas.events.dto.EventDTO;
import com.tfm.bandas.events.exception.BadRequestException;
import com.tfm.bandas.events.model.entity.EventEntity;
import com.tfm.bandas.events.utils.EventStatus;

import java.time.Instant;
import java.util.UUID;

public class EventMapper {

  public static EventEntity toEntityNew(EventCreateRequestDTO req) {
    if (!req.endAt().isAfter(req.startAt())) {
      throw new BadRequestException("end must be after start");
    }

    return EventEntity.builder()
        .id(UUID.randomUUID().toString())
        .title(req.title())
        .description(req.description())
        .location(req.location())
        .type(req.type())
        .status(req.status() == null ? EventStatus.SCHEDULED : req.status())
        .visibility(req.visibility())
        .startAt(req.startAt())
        .endAt(req.endAt())
        .build();
  }

  public static void copyToEntityUpdate(EventCreateRequestDTO req, EventEntity e) {
    if (!req.endAt().isAfter(req.startAt())) {
      throw new BadRequestException("end must be after start");
    }

    e.setTitle(req.title());
    e.setDescription(req.description());
    e.setLocation(req.location());
    e.setType(req.type());
    e.setStatus(req.status());
    e.setVisibility(req.visibility());
    e.setStartAt(req.startAt());
    e.setEndAt(req.endAt());
  }

  public static EventDTO toResponse(EventEntity e) {
    return new EventDTO(
        e.getId(),
        e.getVersion(),
        e.getTitle(),
        e.getDescription(),
        e.getLocation(),
        e.getType(),
        e.getStatus(),
        e.getVisibility(),
        e.getStartAt(),
        e.getEndAt()
    );
  }

  public static CalendarEventItemDTO toCalendarItem(EventEntity e) {
    return new CalendarEventItemDTO(
        e.getId(),
        e.getTitle(),
        e.getStartAt(),
        e.getEndAt(),
        false,
        e.getType(),
        e.getStatus(),
        e.getLocation()
    );
  }
}
