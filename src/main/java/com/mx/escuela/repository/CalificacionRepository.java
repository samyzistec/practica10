package com.mx.escuela.repository;

import com.mx.escuela.model.Calificacion;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

  @Query("""
     select c from Calificacion c
     join fetch c.inscripcion i
     join fetch i.estudiante
     join fetch i.curso cu
     join fetch cu.instructor
     where c.id = :id
  """)
  Optional<Calificacion> findByIdWithRefs(@Param("id") Long id);

  @Query("""
     select c from Calificacion c
     join fetch c.inscripcion i
     join fetch i.estudiante
     join fetch i.curso cu
     join fetch cu.instructor
  """)
  List<Calificacion> findAllWithRefs();

// CalificacionRepository.java
@Query("select avg(c.nota) from Calificacion c")
Double promedioGeneral();



 interface PromedioCursoView {
    String getCurso();
    Double getPromedio();
  }

  @Query("""
    select cu.nombre as curso, avg(c.nota) as promedio
    from Calificacion c
      join c.inscripcion i
      join i.curso cu
    group by cu.nombre
    order by cu.nombre
  """)
  List<PromedioCursoView> promedioPorCurso();
}



