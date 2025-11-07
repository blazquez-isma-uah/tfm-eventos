package com.tfm.bandas.events.controller;

import com.tfm.bandas.events.dto.CalendarEventItemDTO;
import com.tfm.bandas.events.dto.EventCreateRequestDTO;
import com.tfm.bandas.events.dto.EventResponseDTO;
import com.tfm.bandas.events.service.EventService;
import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.tfm.bandas.events.utils.EventVisibility;
import com.tfm.bandas.events.utils.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

  private static final Logger logger = LoggerFactory.getLogger(EventController.class);
  private final EventService eventService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventCreateRequestDTO event) {
    logger.info("Calling createEvent with arguments: {}", event);
    EventResponseDTO response = eventService.createEvent(event);
    logger.info("createEvent returning: {}", response);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{eventId}")
  public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable String eventId, @Valid @RequestBody EventCreateRequestDTO event) {
    logger.info("Calling updateEvent with eventId={}, event={}", eventId, event);
    EventResponseDTO response = eventService.updateEvent(eventId, event);
    logger.info("updateEvent returning: {}", response);
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{eventId}")
  public ResponseEntity<Void> deleteEvent(@PathVariable String eventId) {
    logger.info("Calling deleteEvent with idEvent={}", eventId);
    eventService.deleteEvent(eventId);
    logger.info("deleteEvent Completed successfully");
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping("/{eventId}")
  public ResponseEntity<EventResponseDTO> getEvent(@PathVariable String eventId) {
    logger.info("Calling getEvent with idEvent={}", eventId);
    EventResponseDTO response = eventService.getEvent(eventId);
    logger.info("getEvent returning: {}", response);
    return ResponseEntity.ok(response);
  }

  // Listado por rango (UTC)
  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping
  public ResponseEntity<PaginatedResponse<EventResponseDTO>> listBetweenEvents(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @PageableDefault(size = 10) Pageable pageable
  ) {
    logger.info("Calling listBetweenEvents with from={}, to={}, pageable={}", from, to, pageable);
    PaginatedResponse<EventResponseDTO> response = PaginatedResponse.from(eventService.listEventsBetween(from, to, pageable));
    logger.info("listBetweenEvents returning: {}", response);
    return ResponseEntity.ok(response);
  }

  // Pasados (por endAt)
  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping("/past")
  public ResponseEntity<PaginatedResponse<EventResponseDTO>> listPastEvents(
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
      @PageableDefault(size = 10) Pageable pageable
  ) {
    logger.info("Calling listPastEvents with before={}, pageable={}", before, pageable);
    PaginatedResponse<EventResponseDTO> response = PaginatedResponse.from(eventService.listEventsPast(before != null ? before : Instant.now(), pageable));
    logger.info("listPastEvents returning: {}", response);
    return ResponseEntity.ok(response);
  }

  // Vista calendario (payload ligero)
  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping("/calendar")
  public ResponseEntity<PaginatedResponse<CalendarEventItemDTO>> getPrivateCalendar(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(required = false, name = "tz") String tz,
      @PageableDefault(size = 10) Pageable pageable
  ) {
    logger.info("Calling getPrivateCalendar with from={}, to={}, tz={}, pageable={}", from, to, tz, pageable);
    PaginatedResponse<CalendarEventItemDTO> response = PaginatedResponse.from(eventService.calendarBetween(from, to, tz, pageable));
    logger.info("getPrivateCalendar returning: {}", response);
    return ResponseEntity.ok(response);
  }


  @GetMapping("/public/calendar")
  public ResponseEntity<PaginatedResponse<CalendarEventItemDTO>> getPublicCalendar(
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
          @RequestParam(required = false, name = "tz") String tz,
          @PageableDefault(size = 10) Pageable pageable
  ) {
    logger.info("Calling getPublicCalendar with from={}, to={}, tz={}, pageable={}", from, to, tz, pageable);
    PaginatedResponse<CalendarEventItemDTO> response = PaginatedResponse.from(eventService.calendarBetweenPublic(from, to, tz, pageable));
    logger.info("getPublicCalendar returning: {}", response);
    return ResponseEntity.ok(response);
  }


  @PreAuthorize("hasAnyRole('ADMIN','MUSICIAN')")
  @GetMapping("/search")
  public ResponseEntity<PaginatedResponse<EventResponseDTO>> searchEvents(
          // Texto libre en title/description/location
          @RequestParam(required = false, name = "q") String qText,
          // Filtros específicos
          @RequestParam(required = false) String title,
          @RequestParam(required = false) String description,
          @RequestParam(required = false) String location,
          @RequestParam(required = false, name = "timeZone") String timeZone,
          @RequestParam(required = false) EventType type,
          @RequestParam(required = false) EventStatus status,
          @RequestParam(required = false) EventVisibility visibility,
          // Paginación / ordenación
          @PageableDefault(size = 20, sort = "startAt", direction = Sort.Direction.ASC) Pageable pageable
  ) {
    return ResponseEntity.ok(PaginatedResponse.from(eventService.searchEvents(
            qText, title, description, location, timeZone, type, status, visibility, pageable
    )));
  }

  // Placeholder para partituras (hasta que exista el micro de Partituras)
  @GetMapping("/{eventId}/scores")
  public ResponseEntity<PaginatedResponse<Object>> scoresPlaceholder(@PathVariable String eventId) {
    return ResponseEntity.ok(PaginatedResponse.from(Page.empty())); // devolvemos lista vacía; luego lo integraremos
  }
}
