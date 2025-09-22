package com.tfm.bandas.eventos.service;

import com.tfm.bandas.eventos.dto.CalendarEventItemDTO;
import com.tfm.bandas.eventos.dto.EventCreateRequestDTO;
import com.tfm.bandas.eventos.dto.EventResponseDTO;
import com.tfm.bandas.eventos.dto.EventUpdateRequestDTO;
import com.tfm.bandas.eventos.utils.EventStatus;
import com.tfm.bandas.eventos.utils.EventType;
import com.tfm.bandas.eventos.utils.EventVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface EventService {
  EventResponseDTO createEvent(EventCreateRequestDTO req);
  EventResponseDTO updateEvent(String id, EventUpdateRequestDTO req);
  void deleteEvent(String id);
  EventResponseDTO getEvent(String id);

  Page<EventResponseDTO> listEventsBetween(Instant from, Instant to, Pageable pageable);
  Page<EventResponseDTO> listEventsPast(Instant before, Pageable pageable);

  Page<CalendarEventItemDTO> calendarBetween(Instant from, Instant to, String tzOptional, Pageable pageable);
  Page<CalendarEventItemDTO> calendarBetweenPublic(Instant from, Instant to, String tzOptional, Pageable pageable);

  Page<EventResponseDTO> searchEvents(String qText, String title, String description, String location, String timeZone,
          EventType type, EventStatus status, EventVisibility visibility, Instant from, Instant to, boolean containedInRange,
          Pageable pageable
  );
}
