package com.mx.escuela.service;

import com.mx.escuela.model.Curso;
import com.mx.escuela.model.Instructor;
import com.mx.escuela.repository.CursoRepository;
import com.mx.escuela.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class CursoService {
  private final CursoRepository repo;
  private final InstructorRepository instructorRepo;

  public List<Curso> listar(){ return repo.findAll(); }
  public Curso buscar(Long id){ return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Curso no encontrado: " + id)); }
  public Curso crear(Curso c){
    c.setId(null);
    if(c.getInstructor()!=null && c.getInstructor().getId()!=null){
      Instructor i = instructorRepo.findById(c.getInstructor().getId())
          .orElseThrow(() -> new IllegalArgumentException("Instructor no encontrado: " + c.getInstructor().getId()));
      c.setInstructor(i);
    }
    return repo.save(c);
  }
  public Curso actualizar(Long id, Curso c){
    var db = buscar(id);
    db.setClave(c.getClave());
    db.setNombre(c.getNombre());
    db.setCreditos(c.getCreditos());
    if(c.getInstructor()!=null && c.getInstructor().getId()!=null){
      var i = instructorRepo.findById(c.getInstructor().getId())
          .orElseThrow(() -> new IllegalArgumentException("Instructor no encontrado: " + c.getInstructor().getId()));
      db.setInstructor(i);
    } else {
      db.setInstructor(null);
    }
    return repo.save(db);
  }
  public void eliminar(Long id){ if(!repo.existsById(id)) throw new IllegalArgumentException("Curso no encontrado: " + id); repo.deleteById(id); }
}
