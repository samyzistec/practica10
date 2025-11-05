package com.mx.escuela.service;

import com.mx.escuela.dto.*;
import com.mx.escuela.model.Curso;
import com.mx.escuela.model.Estudiante;
import com.mx.escuela.model.Inscripcion;
import com.mx.escuela.repository.CursoRepository;
import com.mx.escuela.repository.EstudianteRepository;
import com.mx.escuela.repository.InscripcionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepo;
    private final EstudianteRepository estudianteRepo;
    private final CursoRepository cursoRepo;

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionDTO> listar() {
        var lista = inscripcionRepo.findAllWithRefs();
        return lista.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionDTO buscar(Long id) {
        var ins = inscripcionRepo.findByIdWithRefs(id)
                .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada: " + id));
        return toDTO(ins);
    }

    @Override
    public InscripcionDTO crear(InscripcionIn in) {
        Estudiante est = estudianteRepo.findById(in.estudiante().id())
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado: " + in.estudiante().id()));
        Curso cur = cursoRepo.findById(in.curso().id())
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado: " + in.curso().id()));

        var nueva = new Inscripcion();
        nueva.setEstudiante(est);
        nueva.setCurso(cur);
        nueva.setPeriodo(in.periodo());

        var guardada = inscripcionRepo.save(nueva);
        return new InscripcionDTO(
                guardada.getId(),
                new MiniRef(est.getId(), est.getNombre()),
                new MiniRef(cur.getId(), cur.getNombre()),
                guardada.getPeriodo()
        );
    }

    @Override
    public InscripcionDTO actualizar(Long id, InscripcionIn in) {
        var ins = inscripcionRepo.findByIdWithRefs(id)
                .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada: " + id));

        if (in.estudiante() != null && in.estudiante().id() != null) {
            var est = estudianteRepo.findById(in.estudiante().id())
                    .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado: " + in.estudiante().id()));
            ins.setEstudiante(est);
        }
        if (in.curso() != null && in.curso().id() != null) {
            var cur = cursoRepo.findById(in.curso().id())
                    .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado: " + in.curso().id()));
            ins.setCurso(cur);
        }
        if (in.periodo() != null) {
            ins.setPeriodo(in.periodo());
        }

        var upd = inscripcionRepo.save(ins);
        return new InscripcionDTO(
                upd.getId(),
                new MiniRef(upd.getEstudiante().getId(), upd.getEstudiante().getNombre()),
                new MiniRef(upd.getCurso().getId(), upd.getCurso().getNombre()),
                upd.getPeriodo()
        );
    }

    @Override
    public void eliminar(Long id) {
        var ins = inscripcionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada: " + id));
        inscripcionRepo.delete(ins);
    }

    private InscripcionDTO toDTO(Inscripcion i) {
        return new InscripcionDTO(
                i.getId(),
                new MiniRef(i.getEstudiante().getId(), i.getEstudiante().getNombre()),
                new MiniRef(i.getCurso().getId(), i.getCurso().getNombre()),
                i.getPeriodo()
        );
    }
}
