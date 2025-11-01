package com.tfm.bandas.events.service;

import com.tfm.bandas.events.dto.CalendarEventItemDTO;
import com.tfm.bandas.events.dto.EventCreateRequestDTO;
import com.tfm.bandas.events.dto.EventResponseDTO;
import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.tfm.bandas.events.utils.EventVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface EventService {
  EventResponseDTO createEvent(EventCreateRequestDTO event);
  EventResponseDTO updateEvent(String eventId, EventCreateRequestDTO event);
  void deleteEvent(String eventId);
  EventResponseDTO getEvent(String idEvent);

  Page<EventResponseDTO> listEventsBetween(Instant from, Instant to, Pageable pageable);
  Page<EventResponseDTO> listEventsPast(Instant before, Pageable pageable);

  Page<CalendarEventItemDTO> calendarBetween(Instant from, Instant to, String tzOptional, Pageable pageable);
  Page<CalendarEventItemDTO> calendarBetweenPublic(Instant from, Instant to, String tzOptional, Pageable pageable);

  Page<EventResponseDTO> searchEvents(String qText, String title, String description, String location, String timeZone,
          EventType type, EventStatus status, EventVisibility visibility, Pageable pageable);
}
