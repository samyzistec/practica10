package com.mx.escuela.service;

import com.mx.escuela.dto.InscripcionDTO;
import com.mx.escuela.dto.InscripcionIn;

import java.util.List;

public interface InscripcionService {
    List<InscripcionDTO> listar();
    InscripcionDTO buscar(Long id);
    InscripcionDTO crear(InscripcionIn in);
    InscripcionDTO actualizar(Long id, InscripcionIn in);
    void eliminar(Long id);
}
