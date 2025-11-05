package com.mx.escuela.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record InscripcionIn(
        @NotNull IdRef estudiante,
        @NotNull IdRef curso,
        @NotBlank String periodo
) {}
