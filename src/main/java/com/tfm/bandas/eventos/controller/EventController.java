package com.tfm.bandas.eventos.controller;

import com.tfm.bandas.eventos.dto.CalendarEventItemDTO;
import com.tfm.bandas.eventos.dto.EventCreateRequestDTO;
import com.tfm.bandas.eventos.dto.EventResponseDTO;
import com.tfm.bandas.eventos.service.EventService;
import com.tfm.bandas.eventos.utils.EventStatus;
import com.tfm.bandas.eventos.utils.EventType;
import com.tfm.bandas.eventos.utils.EventVisibility;
import com.tfm.bandas.eventos.utils.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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
  public EventResponseDTO create(@Valid @RequestBody EventCreateRequestDTO req) {
    logger.info("Calling method: create, Arguments: req={}", req);
    EventResponseDTO response = eventService.createEvent(req);
    logger.info("Method: create, Returning: {}", response);
    return response;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public EventResponseDTO update(@PathVariable String id, @Valid @RequestBody EventCreateRequestDTO req) {
    logger.info("Calling method: update, Arguments: id={}, req={}", id, req);
    EventResponseDTO response = eventService.updateEvent(id, req);
    logger.info("Method: update, Returning: {}", response);
    return response;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable String id) {
    logger.info("Calling method: delete, Arguments: id={}", id);
    eventService.deleteEvent(id);
    logger.info("Method: delete, Completed successfully");
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping("/{id}")
  public EventResponseDTO get(@PathVariable String id) {
    logger.info("Calling method: get, Arguments: id={}", id);
    EventResponseDTO response = eventService.getEvent(id);
    logger.info("Method: get, Returning: {}", response);
    return response;
  }

  // Listado por rango (UTC)
  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping
  public PaginatedResponse<EventResponseDTO> listBetween(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @PageableDefault(size = 10) Pageable pageable
  ) {
    return PaginatedResponse.from(eventService.listEventsBetween(from, to, pageable));
  }

  // Pasados (por endAt)
  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping("/past")
  public PaginatedResponse<EventResponseDTO> listPast(
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
      @PageableDefault(size = 10) Pageable pageable
  ) {
    return PaginatedResponse.from(eventService.listEventsPast(before != null ? before : Instant.now(), pageable));
  }

  // Vista calendario (payload ligero)
  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping("/calendar")
  public PaginatedResponse<CalendarEventItemDTO> calendar(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(required = false, name = "tz") String tz,
      @PageableDefault(size = 10) Pageable pageable
  ) {
    return PaginatedResponse.from(eventService.calendarBetween(from, to, tz, pageable));
  }

  @GetMapping("/public/calendar")
  public PaginatedResponse<CalendarEventItemDTO> publicCalendar(
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
          @RequestParam(required = false, name = "tz") String tz,
          @PageableDefault(size = 10) Pageable pageable
  ) {
    return PaginatedResponse.from(eventService.calendarBetweenPublic(from, to, tz, pageable));
  }


  @PreAuthorize("hasAnyRole('ADMIN','MUSICIAN')")
  @GetMapping("/search")
  public PaginatedResponse<EventResponseDTO> searchEvents(
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
    return PaginatedResponse.from(eventService.searchEvents(
            qText, title, description, location, timeZone, type, status, visibility, pageable
    ));
  }

  // Placeholder para partituras (hasta que exista el micro de Partituras)
  @GetMapping("/{id}/scores")
  public PaginatedResponse<Object> scoresPlaceholder(@PathVariable String id) {
    return PaginatedResponse.from(Page.empty()); // devolvemos lista vacía; luego lo integraremos
  }
}
