package com.tfm.bandas.eventos.service;

import com.tfm.bandas.eventos.dto.CalendarEventItem;
import com.tfm.bandas.eventos.dto.EventCreateRequest;
import com.tfm.bandas.eventos.dto.EventResponse;
import com.tfm.bandas.eventos.dto.EventUpdateRequest;

import java.time.Instant;
import java.util.List;

public interface EventService {
  EventResponse create(EventCreateRequest req);
  EventResponse update(String id, EventUpdateRequest req);
  void delete(String id);
  EventResponse get(String id);

  List<EventResponse> listBetween(Instant from, Instant to);
  List<EventResponse> listPast(Instant before);

  List<CalendarEventItem> calendarBetween(Instant from, Instant to, String tzOptional);
  List<CalendarEventItem> calendarBetweenPublic(Instant from, Instant to, String tzOptional);
}
