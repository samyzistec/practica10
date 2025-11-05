package com.mx.escuela.controller;

import com.mx.escuela.model.Estudiante;
import com.mx.escuela.service.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {
  private final EstudianteService service;

  @GetMapping public List<Estudiante> listar(){ return service.listar(); }
  @GetMapping("/{id}") public Estudiante buscar(@PathVariable Long id){ return service.buscar(id); }

  @PostMapping public ResponseEntity<Estudiante> crear(@RequestBody Estudiante e){
    var saved = service.crear(e);
    return ResponseEntity.created(URI.create("/api/estudiantes/" + saved.getId())).body(saved);
  }
  @PutMapping("/{id}") public Estudiante actualizar(@PathVariable Long id, @RequestBody Estudiante e){ return service.actualizar(id, e); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){ service.eliminar(id); return ResponseEntity.noContent().build(); }
}
