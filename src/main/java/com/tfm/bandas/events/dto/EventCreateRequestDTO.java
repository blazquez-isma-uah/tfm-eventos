package com.tfm.bandas.events.dto;

import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.tfm.bandas.events.utils.EventVisibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.Instant;

public record EventCreateRequestDTO(
        @JsonProperty("title")
        @NotBlank(message = "El título es obligatorio.")
        @Size(max = 200, message = "El título no puede superar los 200 caracteres.")
        String title,

        @JsonProperty("description")
        @Size(max = 5000, message = "La descripción no puede superar los 5000 caracteres.")
        String description,

        @JsonProperty("location")
        @Size(max = 255, message = "La ubicación no puede superar los 255 caracteres.")
        String location,

        @JsonProperty("type")
        @NotNull(message = "El tipo de evento es obligatorio.")
        EventType type,

        @JsonProperty("status") EventStatus status, // opcional; si null => SCHEDULED

        @JsonProperty("visibility")
        @NotNull(message = "La visibilidad del evento es obligatoria.")
        EventVisibility visibility,

        @JsonProperty("startAt")
        @NotNull(message = "La fecha de inicio es obligatoria.")
        Instant startAt,

        @JsonProperty("endAt")
        @NotNull(message = "La fecha de fin es obligatoria.")
        Instant endAt
) {}
