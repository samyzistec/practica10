package com.mx.escuela.controller;

import com.mx.escuela.dto.CalificacionDTO;
import com.mx.escuela.dto.CalificacionIn;
import com.mx.escuela.service.CalificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {
  private final CalificacionService service;

  @GetMapping public List<CalificacionDTO> listar(){ return service.listar(); }
  @GetMapping("/{id}") public CalificacionDTO buscar(@PathVariable Long id){ return service.buscar(id); }

  @PostMapping public ResponseEntity<CalificacionDTO> crear(@Valid @RequestBody CalificacionIn in){
    var dto = service.crear(in);
    return ResponseEntity.created(URI.create("/api/calificaciones/" + dto.id())).body(dto);
  }
  @PutMapping("/{id}") public CalificacionDTO actualizar(@PathVariable Long id, @Valid @RequestBody CalificacionIn in){ return service.actualizar(id, in); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){ service.eliminar(id); return ResponseEntity.noContent().build(); }
   @GetMapping("/promedio")
  public Map<String, Object> promedioGeneral() {
    double promedio = service.promedioGeneral();
    return Map.of("promedio", promedio); 
  }
  @GetMapping("/promedio-por-curso")
  public List<Map<String,Object>> promedioPorCurso(){
    return service.promedioPorCurso();
  }

}
