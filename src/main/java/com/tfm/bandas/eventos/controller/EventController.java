package com.tfm.bandas.eventos.controller;

import com.tfm.bandas.eventos.dto.CalendarEventItem;
import com.tfm.bandas.eventos.dto.EventCreateRequest;
import com.tfm.bandas.eventos.dto.EventResponse;
import com.tfm.bandas.eventos.dto.EventUpdateRequest;
import com.tfm.bandas.eventos.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

  private final EventService service;

  @PostMapping
  public EventResponse create(@Valid @RequestBody EventCreateRequest req) {
    return service.create(req);
  }

  @PutMapping("/{id}")
  public EventResponse update(@PathVariable String id, @Valid @RequestBody EventUpdateRequest req) {
    return service.update(id, req);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable String id) {
    service.delete(id);
  }

  @GetMapping("/{id}")
  public EventResponse get(@PathVariable String id) {
    return service.get(id);
  }

  // Listado por rango (UTC)
  @GetMapping
  public List<EventResponse> listBetween(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
  ) {
    return service.listBetween(from, to);
  }

  // Pasados (por endAt)
  @GetMapping("/past")
  public List<EventResponse> listPast(
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before
  ) {
    return service.listPast(before != null ? before : Instant.now());
  }

  // Vista calendario (payload ligero)
  @GetMapping("/calendar")
  public List<CalendarEventItem> calendar(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(required = false, name = "tz") String tz
  ) {
    return service.calendarBetween(from, to, tz);
  }

  // Placeholder para partituras (hasta que exista el micro de Partituras)
  @GetMapping("/{id}/scores")
  public List<Object> scoresPlaceholder(@PathVariable String id) {
    return List.of(); // devolvemos lista vacía; luego lo integraremos
  }
}
