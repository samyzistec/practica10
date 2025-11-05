package com.mx.escuela.service;

import com.mx.escuela.model.Estudiante;
import com.mx.escuela.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class EstudianteService {
  private final EstudianteRepository repo;

  public List<Estudiante> listar(){ return repo.findAll(); }
  public Estudiante buscar(Long id){ return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado: " + id)); }
  public Estudiante crear(Estudiante e){ e.setId(null); return repo.save(e); }
  public Estudiante actualizar(Long id, Estudiante e){ var db = buscar(id); db.setNombre(e.getNombre()); db.setEmail(e.getEmail()); db.setFechaIngreso(e.getFechaIngreso()); return repo.save(db); }
  public void eliminar(Long id){ if(!repo.existsById(id)) throw new IllegalArgumentException("Estudiante no encontrado: " + id); repo.deleteById(id); }
}
