package com.mx.escuela.service;

import com.mx.escuela.dto.CalificacionDTO;
import com.mx.escuela.dto.CalificacionDTO.InscripcionLite;
import com.mx.escuela.dto.CalificacionIn;
import com.mx.escuela.dto.MiniRef;
import com.mx.escuela.model.Calificacion;
import com.mx.escuela.model.Inscripcion;
import com.mx.escuela.repository.CalificacionRepository;
import com.mx.escuela.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional // por defecto de escritura
public class CalificacionService {

  private final CalificacionRepository repo;
  private final InscripcionRepository insRepo;

  public CalificacionDTO crear(CalificacionIn in){
    var c = new Calificacion();
    c.setInscripcion(refInscripcion(requiredId(in)));
    c.setNota(in.nota());
    c = repo.save(c);
    // cargar refs para el DTO
    return toDTO(repo.findByIdWithRefs(c.getId()).orElse(c));
  }

  public CalificacionDTO actualizar(Long id, CalificacionIn in){
    var c = repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Calificación no encontrada: " + id));

    // si viene inscripcion, cambia la ref
    if (in.inscripcion() != null && in.inscripcion().id() != null) {
      c.setInscripcion(refInscripcion(in.inscripcion().id()));
    }
    // si viene nota, actualiza
    if (in.nota() != null) {
      c.setNota(in.nota());
    }

    repo.save(c); // explícito
    return toDTO(repo.findByIdWithRefs(id).orElse(c));
  }

  public void eliminar(Long id){
    if (!repo.existsById(id)) {
      throw new IllegalArgumentException("Calificación no encontrada: " + id);
    }
    repo.deleteById(id);
  }

  @Transactional(readOnly = true)
  public CalificacionDTO buscar(Long id){
    var c = repo.findByIdWithRefs(id)
        .orElseThrow(() -> new IllegalArgumentException("Calificación no encontrada: " + id));
    return toDTO(c);
  }

  @Transactional(readOnly = true)
  public List<CalificacionDTO> listar(){
    return repo.findAllWithRefs().stream().map(this::toDTO).toList();
  }

  // helpers
  private Long requiredId(CalificacionIn in) {
    if (in.inscripcion() == null || in.inscripcion().id() == null) {
      throw new IllegalArgumentException("Debe indicar la inscripción (id).");
    }
    return in.inscripcion().id();
  }

  private Inscripcion refInscripcion(Long id){
    return insRepo.findByIdWithRefs(id)
        .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada: " + id));
  }

  private CalificacionDTO toDTO(Calificacion c){
    var i = c.getInscripcion();
    var est = new MiniRef(i.getEstudiante().getId(), i.getEstudiante().getNombre());
    var cur = new MiniRef(i.getCurso().getId(), i.getCurso().getNombre());
    var lite = new InscripcionLite(i.getId(), est, cur);
    return new CalificacionDTO(c.getId(), lite, c.getNota());
  }

  // CalificacionService.java (agrega este método público)
@Transactional(readOnly = true)
public Double promedioGeneral() {
  return Optional.ofNullable(repo.promedioGeneral()).orElse(0.0);
}

  public List<Map<String,Object>> promedioPorCurso(){
    return repo.promedioPorCurso().stream()
      .map(v -> Map.<String,Object>of("curso", v.getCurso(), "promedio", v.getPromedio()))
      .toList();
  }

}
