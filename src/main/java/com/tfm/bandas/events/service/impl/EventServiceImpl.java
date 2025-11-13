package com.tfm.bandas.events.service.impl;

import com.tfm.bandas.events.config.EventRulesProperties;
import com.tfm.bandas.events.dto.CalendarEventItemDTO;
import com.tfm.bandas.events.dto.EventCreateRequestDTO;
import com.tfm.bandas.events.dto.EventResponseDTO;
import com.tfm.bandas.events.dto.mapper.EventMapper;
import com.tfm.bandas.events.exception.BadRequestException;
import com.tfm.bandas.events.exception.NotFoundException;
import com.tfm.bandas.events.model.entity.EventEntity;
import com.tfm.bandas.events.model.repository.EventRepository;
import com.tfm.bandas.events.model.specification.EventSpecifications;
import com.tfm.bandas.events.service.EventService;
import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.tfm.bandas.events.utils.EventVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepo;
  private final EventRulesProperties rules;

  @Override
  public EventResponseDTO createEvent(EventCreateRequestDTO event) {
    EventEntity saved = EventMapper.toEntityNew(event);
    validateBusinessRules(saved, null);
    return EventMapper.toResponse(eventRepo.save(saved));
  }

  @Override
  public EventResponseDTO updateEvent(String eventId, EventCreateRequestDTO event) {
    EventEntity e = eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
    EventMapper.copyToEntityUpdate(event, e);
    validateBusinessRules(e, eventId);
    return EventMapper.toResponse(eventRepo.save(e));
  }

  @Override
  public void deleteEvent(String eventId) {
    if (!eventRepo.existsById(eventId)) throw new NotFoundException("Event not found: " + eventId);
    eventRepo.deleteById(eventId);
  }

  @Override
  @Transactional(readOnly = true)
  public EventResponseDTO getEvent(String idEvent) {
    return eventRepo.findById(idEvent)
        .map(EventMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Event not found: " + idEvent));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<EventResponseDTO> listEventsBetween(Instant from, Instant to, Pageable pageable) {
    return eventRepo.findAllByStartAtBetween(from, to, pageable)
        .map(EventMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<EventResponseDTO> listEventsPast(Instant before, Pageable pageable) {
    return eventRepo.findAllByEndAtBefore(before, pageable)
        .map(EventMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CalendarEventItemDTO> calendarBetween(Instant from, Instant to, String tzOptional, Pageable pageable) {
    ZoneId zone = (tzOptional == null || tzOptional.isBlank()) ? null : ZoneId.of(tzOptional);
    return eventRepo.findAllByStartAtBetween(from, to, pageable)
        .map(e -> EventMapper.toCalendarItem(e, zone));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CalendarEventItemDTO> calendarBetweenPublic(Instant from, Instant to, String tzOptional, Pageable pageable) {
    ZoneId zone = (tzOptional == null || tzOptional.isBlank()) ? null : ZoneId.of(tzOptional);
    return eventRepo.findAllByVisibilityAndStartAtBetween(EventVisibility.PUBLIC, from, to, pageable)
            .map(e -> EventMapper.toCalendarItem(e, zone));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<EventResponseDTO> searchEvents(String qText, String title, String description, String location, String timeZone,
          EventType type, EventStatus status, EventVisibility visibility, Pageable pageable) {

    Specification<EventEntity> spec = Specification.allOf(
            EventSpecifications.all(),
            EventSpecifications.text(qText),
            EventSpecifications.titleContains(title),
            EventSpecifications.descriptionContains(description),
            EventSpecifications.locationContains(location),
            EventSpecifications.timeZoneEquals(timeZone),
            EventSpecifications.typeEquals(type),
            EventSpecifications.statusEquals(status),
            EventSpecifications.visibilityEquals(visibility));

    return eventRepo.findAll(spec, pageable).map(EventMapper::toResponse);
  }


  private void validateBusinessRules(EventEntity eventEntity, String excludeIdForUpdate) {
    // start < end
    if (!eventEntity.getEndAt().isAfter(eventEntity.getStartAt())) {
      throw new BadRequestException("end must be strictly after start");
    }

    // Duraciones mínimas/máximas
    long minutes = Duration.between(eventEntity.getStartAt(), eventEntity.getEndAt()).toMinutes();
    if (minutes < rules.minDurationMinutes()) {
      throw new BadRequestException("duration too short: min " + rules.minDurationMinutes() + " minutes");
    }
    if (minutes > rules.maxDurationHours() * 60L) {
      throw new BadRequestException("duration too long: max " + rules.maxDurationHours() + " hours");
    }

    // Crear en pasado
    if (!rules.allowCreateInPast() && eventEntity.getStartAt().isBefore(Instant.now())) {
      throw new BadRequestException("creating events in the past is disabled by policy");
    }

    // Solapes por ubicación (si hay ubicación y no se permite solape)
    if (!rules.allowOverlapSameLocation() && eventEntity.getLocation() != null && !eventEntity.getLocation().isBlank()) {
      long conflicts = eventRepo.countConflictsAtLocation(
              eventEntity.getLocation(), eventEntity.getStartAt(), eventEntity.getEndAt(), excludeIdForUpdate);
      if (conflicts > 0) {
        throw new BadRequestException("time slot overlaps with another event at the same location");
      }
    }
  }
}
