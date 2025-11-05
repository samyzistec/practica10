package com.mx.escuela.repository;

import com.mx.escuela.model.Inscripcion;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

  @Query("""
     select i from Inscripcion i
     join fetch i.estudiante
     join fetch i.curso c
     join fetch c.instructor
  """)
  List<Inscripcion> findAllWithRefs();

  @Query("""
     select i from Inscripcion i
     join fetch i.estudiante
     join fetch i.curso c
     join fetch c.instructor
     where i.id = :id
  """)
  Optional<Inscripcion> findByIdWithRefs(@Param("id") Long id);
}
