package com.tfm.bandas.eventos.controller;

import com.tfm.bandas.eventos.dto.CalendarEventItemDTO;
import com.tfm.bandas.eventos.service.EventService;
import com.tfm.bandas.eventos.utils.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/events")
public class PublicEventController {

    private final EventService service;

    @GetMapping("/calendar")
    public PaginatedResponse<CalendarEventItemDTO> publicCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false, name = "tz") String tz,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return PaginatedResponse.from(service.calendarBetweenPublic(from, to, tz, pageable));
    }

}
