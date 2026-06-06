package com.tfm.bandas.events.dto;

import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.tfm.bandas.events.utils.EventVisibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.time.Instant;

public record EventCreateRequestDTO(
        @JsonProperty("title") @NotBlank @Size(max = 200) String title,
        @JsonProperty("description") @Size(max = 5000) String description,
        @JsonProperty("location") @Size(max = 255) String location,
        @JsonProperty("type") @NotNull EventType type,
        @JsonProperty("status") EventStatus status, // opcional; si null => SCHEDULED
        @JsonProperty("visibility") @NotNull EventVisibility visibility,

        @JsonProperty("startAt") @NotNull Instant startAt,
        @JsonProperty("endAt") @NotNull Instant endAt
) {}
