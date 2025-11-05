package com.mx.escuela.dto;

public record InscripcionDTO(
        Long id,
        MiniRef estudiante,
        MiniRef curso,
        String periodo
) {}
