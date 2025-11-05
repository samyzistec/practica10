package com.mx.escuela.controller;

import com.mx.escuela.dto.InscripcionDTO;
import com.mx.escuela.dto.InscripcionIn;
import com.mx.escuela.service.InscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {
  private final InscripcionService service;

  @GetMapping public List<InscripcionDTO> listar(){ return service.listar(); }
  @GetMapping("/{id}") public InscripcionDTO buscar(@PathVariable Long id){ return service.buscar(id); }

  @PostMapping public ResponseEntity<InscripcionDTO> crear(@Valid @RequestBody InscripcionIn in){
    var dto = service.crear(in);
    return ResponseEntity.created(URI.create("/api/inscripciones/" + dto.id())).body(dto);
  }
  @PutMapping("/{id}") public InscripcionDTO actualizar(@PathVariable Long id, @Valid @RequestBody InscripcionIn in){ return service.actualizar(id, in); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){ service.eliminar(id); return ResponseEntity.noContent().build(); }
}
