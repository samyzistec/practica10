package com.mx.escuela.repository;
import com.mx.escuela.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CursoRepository extends JpaRepository<Curso, Long> {}
