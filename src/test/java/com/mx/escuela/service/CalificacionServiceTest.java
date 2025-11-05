
package com.mx.escuela.service;

import com.mx.escuela.dto.CalificacionDTO;
import com.mx.escuela.dto.CalificacionIn;
import com.mx.escuela.dto.IdRef;
import com.mx.escuela.model.Calificacion;
import com.mx.escuela.model.Curso;
import com.mx.escuela.model.Estudiante;
import com.mx.escuela.model.Inscripcion;
import com.mx.escuela.repository.CalificacionRepository;
import com.mx.escuela.repository.InscripcionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceTest {

  @Mock private CalificacionRepository repo;
  @Mock private InscripcionRepository insRepo;
  @InjectMocks private CalificacionService service;

  private Estudiante est(Long id, String n){ var e=new Estudiante(); e.setId(id); e.setNombre(n); return e; }
  private Curso cur(Long id, String n){ var c=new Curso(); c.setId(id); c.setNombre(n); return c; }
  private Inscripcion ins(Long id){ var i=new Inscripcion(); i.setId(id); i.setEstudiante(est(10L,"Ana")); i.setCurso(cur(5L,"BD")); return i; }

  @Test void listar_ok(){
    var i = ins(1L);
    var c = new Calificacion(); c.setId(7L); c.setInscripcion(i); c.setNota(new BigDecimal("95.5"));
    when(repo.findAllWithRefs()).thenReturn(List.of(c));

    var out = service.listar();
    assertThat(out).hasSize(1);
    CalificacionDTO dto = out.get(0);
    assertThat(dto.inscripcion().estudiante().nombre()).isEqualTo("Ana");
  }

  @Test void crear_ok(){
    var in = new CalificacionIn(new IdRef(1L), new BigDecimal("80.0"));
    var i = ins(1L);
    when(insRepo.findByIdWithRefs(1L)).thenReturn(Optional.of(i));

    var c = new Calificacion(); c.setId(7L); c.setInscripcion(i); c.setNota(new BigDecimal("80.0"));
    when(repo.save(any(Calificacion.class))).thenReturn(c);
    when(repo.findByIdWithRefs(7L)).thenReturn(Optional.of(c));

    var dto = service.crear(in);
    assertThat(dto.id()).isEqualTo(7L);
  }
}
