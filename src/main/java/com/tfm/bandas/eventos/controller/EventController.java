package com.tfm.bandas.eventos.controller;

import com.tfm.bandas.eventos.dto.CalendarEventItemDTO;
import com.tfm.bandas.eventos.dto.EventCreateRequestDTO;
import com.tfm.bandas.eventos.dto.EventResponseDTO;
import com.tfm.bandas.eventos.dto.EventUpdateRequestDTO;
import com.tfm.bandas.eventos.service.EventService;
import com.tfm.bandas.eventos.utils.EventStatus;
import com.tfm.bandas.eventos.utils.EventType;
import com.tfm.bandas.eventos.utils.EventVisibility;
import com.tfm.bandas.eventos.utils.PaginatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

  private final EventService eventService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public EventResponseDTO create(@Valid @RequestBody EventCreateRequestDTO req) {
    return eventService.createEvent(req);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public EventResponseDTO update(@PathVariable String id, @Valid @RequestBody EventUpdateRequestDTO req) {
    return eventService.updateEvent(id, req);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable String id) {
    eventService.deleteEvent(id);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MUSICIAN')")
  @GetMapping("/{id}")
  public EventResponseDTO get(@PathVariable String id) {
    return eventService.getEvent(id);
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
          // Rango temporal
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
          @RequestParam(required = false, defaultValue = "false") boolean containedInRange,
          // Paginación / ordenación
          @PageableDefault(size = 20, sort = "startAt,asc") Pageable pageable
  ) {
    return PaginatedResponse.from(eventService.searchEvents(
            qText, title, description, location, timeZone, type, status, visibility, from, to, containedInRange, pageable
    ));
  }

  // Placeholder para partituras (hasta que exista el micro de Partituras)
  @GetMapping("/{id}/scores")
  public PaginatedResponse<Object> scoresPlaceholder(@PathVariable String id) {
    return PaginatedResponse.from(Page.empty()); // devolvemos lista vacía; luego lo integraremos
  }
}
