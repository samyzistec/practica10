package com.mx.escuela.repository;
import com.mx.escuela.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InstructorRepository extends JpaRepository<Instructor, Long> {}
