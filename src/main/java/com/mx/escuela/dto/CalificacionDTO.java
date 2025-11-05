package com.mx.escuela.dto;

import java.math.BigDecimal;

public record CalificacionDTO(
    Long id,
    InscripcionLite inscripcion,
    BigDecimal nota
) {
  public record InscripcionLite(Long id, MiniRef estudiante, MiniRef curso) {}
}
