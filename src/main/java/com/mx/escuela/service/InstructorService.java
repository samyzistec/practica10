package com.mx.escuela.service;

import com.mx.escuela.model.Instructor;
import com.mx.escuela.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class InstructorService {
  private final InstructorRepository repo;

  public List<Instructor> listar(){ return repo.findAll(); }
  public Instructor buscar(Long id){ return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Instructor no encontrado: " + id)); }
  public Instructor crear(Instructor i){ i.setId(null); return repo.save(i); }
  public Instructor actualizar(Long id, Instructor i){ var db = buscar(id); db.setNombre(i.getNombre()); db.setEmail(i.getEmail()); return repo.save(db); }
  public void eliminar(Long id){ if(!repo.existsById(id)) throw new IllegalArgumentException("Instructor no encontrado: " + id); repo.deleteById(id); }
}
