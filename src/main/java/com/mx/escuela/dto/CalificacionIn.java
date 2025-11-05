package com.mx.escuela.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CalificacionIn(
    @NotNull IdRef inscripcion,
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal nota
) {}
