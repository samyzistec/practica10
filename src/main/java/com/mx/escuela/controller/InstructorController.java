package com.mx.escuela.controller;

import com.mx.escuela.model.Instructor;
import com.mx.escuela.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/instructores")
@RequiredArgsConstructor
public class InstructorController {
  private final InstructorService service;

  @GetMapping public List<Instructor> listar(){ return service.listar(); }
  @GetMapping("/{id}") public Instructor buscar(@PathVariable Long id){ return service.buscar(id); }

  @PostMapping public ResponseEntity<Instructor> crear(@RequestBody Instructor i){
    var saved = service.crear(i);
    return ResponseEntity.created(URI.create("/api/instructores/" + saved.getId())).body(saved);
  }
  @PutMapping("/{id}") public Instructor actualizar(@PathVariable Long id, @RequestBody Instructor i){ return service.actualizar(id, i); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){ service.eliminar(id); return ResponseEntity.noContent().build(); }
}
