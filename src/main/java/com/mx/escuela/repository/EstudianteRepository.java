package com.mx.escuela.repository;
import com.mx.escuela.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {}
