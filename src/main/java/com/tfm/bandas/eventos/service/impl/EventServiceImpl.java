package com.tfm.bandas.eventos.service.impl;

import com.tfm.bandas.eventos.dto.CalendarEventItem;
import com.tfm.bandas.eventos.dto.EventCreateRequest;
import com.tfm.bandas.eventos.dto.EventResponse;
import com.tfm.bandas.eventos.dto.EventUpdateRequest;
import com.tfm.bandas.eventos.dto.mapper.EventMapper;
import com.tfm.bandas.eventos.exception.NotFoundException;
import com.tfm.bandas.eventos.model.entity.Event;
import com.tfm.bandas.eventos.model.repository.EventRepository;
import com.tfm.bandas.eventos.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

  private final EventRepository repo;

  @Override
  public EventResponse create(EventCreateRequest req) {
    Event saved = repo.save(EventMapper.toEntityNew(req));
    return EventMapper.toResponse(saved);
  }

  @Override
  public EventResponse update(String id, EventUpdateRequest req) {
    Event e = repo.findById(id).orElseThrow(() -> new NotFoundException("Event not found: " + id));
    EventMapper.copyToEntityUpdate(req, e);
    return EventMapper.toResponse(e);
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
}
