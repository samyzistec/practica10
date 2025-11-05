package com.mx.escuela.controller;

import com.mx.escuela.model.Curso;
import com.mx.escuela.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {
  private final CursoService service;

  @GetMapping public List<Curso> listar(){ return service.listar(); }
  @GetMapping("/{id}") public Curso buscar(@PathVariable Long id){ return service.buscar(id); }

  @PostMapping public ResponseEntity<Curso> crear(@RequestBody Curso c){
    var saved = service.crear(c);
    return ResponseEntity.created(URI.create("/api/cursos/" + saved.getId())).body(saved);
  }
  @PutMapping("/{id}") public Curso actualizar(@PathVariable Long id, @RequestBody Curso c){ return service.actualizar(id, c); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){ service.eliminar(id); return ResponseEntity.noContent().build(); }
}
