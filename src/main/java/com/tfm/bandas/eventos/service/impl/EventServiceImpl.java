package com.tfm.bandas.eventos.service.impl;

import com.tfm.bandas.eventos.config.EventRulesProperties;
import com.tfm.bandas.eventos.dto.CalendarEventItem;
import com.tfm.bandas.eventos.dto.EventCreateRequest;
import com.tfm.bandas.eventos.dto.EventResponse;
import com.tfm.bandas.eventos.dto.EventUpdateRequest;
import com.tfm.bandas.eventos.dto.mapper.EventMapper;
import com.tfm.bandas.eventos.exception.BadRequestException;
import com.tfm.bandas.eventos.exception.NotFoundException;
import com.tfm.bandas.eventos.model.entity.Event;
import com.tfm.bandas.eventos.model.repository.EventRepository;
import com.tfm.bandas.eventos.service.EventService;
import com.tfm.bandas.eventos.utils.EventVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

  private final EventRepository repo;
  private final EventRulesProperties rules;

  @Override
  public EventResponse create(EventCreateRequest req) {
    Event saved = EventMapper.toEntityNew(req);
    validateBusinessRules(saved, null);
    return EventMapper.toResponse(repo.save(saved));
  }

  @Override
  public EventResponse update(String id, EventUpdateRequest req) {
    Event e = repo.findById(id).orElseThrow(() -> new NotFoundException("Event not found: " + id));
    EventMapper.copyToEntityUpdate(req, e);
    validateBusinessRules(e, id);
    return EventMapper.toResponse(repo.save(e));
  }

  @Override
  public void delete(String id) {
    if (!repo.existsById(id)) throw new NotFoundException("Event not found: " + id);
    repo.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public EventResponse get(String id) {
    return repo.findById(id)
        .map(EventMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Event not found: " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<EventResponse> listBetween(Instant from, Instant to) {
    return repo.findAllByStartAtBetweenOrderByStartAtAsc(from, to)
        .stream().map(EventMapper::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<EventResponse> listPast(Instant before) {
    return repo.findAllByEndAtBeforeOrderByEndAtDesc(before)
        .stream().map(EventMapper::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<CalendarEventItem> calendarBetween(Instant from, Instant to, String tzOptional) {
    ZoneId zone = (tzOptional == null || tzOptional.isBlank()) ? null : ZoneId.of(tzOptional);
    return repo.findAllByStartAtBetweenOrderByStartAtAsc(from, to)
        .stream().map(e -> EventMapper.toCalendarItem(e, zone)).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<CalendarEventItem> calendarBetweenPublic(Instant from, Instant to, String tzOptional) {
    ZoneId zone = (tzOptional == null || tzOptional.isBlank()) ? null : ZoneId.of(tzOptional);
    return repo.findAllByVisibilityAndStartAtBetweenOrderByStartAtAsc(
            EventVisibility.PUBLIC, from, to
    ).stream().map(e -> EventMapper.toCalendarItem(e, zone)).toList();
  }

  private void validateBusinessRules(Event event, String excludeIdForUpdate) {
    // start < end
    if (!event.getEndAt().isAfter(event.getStartAt())) {
      throw new BadRequestException("end must be strictly after start");
    }

    // Duraciones mínimas/máximas
    long minutes = Duration.between(event.getStartAt(), event.getEndAt()).toMinutes();
    if (minutes < rules.minDurationMinutes()) {
      throw new BadRequestException("duration too short: min " + rules.minDurationMinutes() + " minutes");
    }
    if (minutes > rules.maxDurationHours() * 60L) {
      throw new BadRequestException("duration too long: max " + rules.maxDurationHours() + " hours");
    }

    // Crear en pasado
    if (!rules.allowCreateInPast() && event.getStartAt().isBefore(Instant.now())) {
      throw new BadRequestException("creating events in the past is disabled by policy");
    }

    // Solapes por ubicación (si hay ubicación y no se permite solape)
    if (!rules.allowOverlapSameLocation() && event.getLocation() != null && !event.getLocation().isBlank()) {
      long conflicts = repo.countConflictsAtLocation(
              event.getLocation(), event.getStartAt(), event.getEndAt(), excludeIdForUpdate);
      if (conflicts > 0) {
        throw new BadRequestException("time slot overlaps with another event at the same location");
      }
    }
  }
}
