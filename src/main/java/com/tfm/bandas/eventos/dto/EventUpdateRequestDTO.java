package com.tfm.bandas.eventos.dto;

import com.tfm.bandas.eventos.utils.EventStatus;
import com.tfm.bandas.eventos.utils.EventType;
import com.tfm.bandas.eventos.utils.EventVisibility;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record EventUpdateRequestDTO(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 5000) String description,
    @Size(max = 255) String location,
    @NotNull EventType type,
    @NotNull EventStatus status,
    @NotNull EventVisibility visibility,
    @NotNull LocalDateTime localStart,
    @NotNull LocalDateTime localEnd,
    @NotBlank String timeZone
) {}
