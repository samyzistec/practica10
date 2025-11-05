package com.mx.escuela.service;

import com.mx.escuela.dto.IdRef;
import com.mx.escuela.dto.InscripcionDTO;
import com.mx.escuela.dto.InscripcionIn;
import com.mx.escuela.model.Curso;
import com.mx.escuela.model.Estudiante;
import com.mx.escuela.model.Inscripcion;
import com.mx.escuela.repository.CursoRepository;
import com.mx.escuela.repository.EstudianteRepository;
import com.mx.escuela.repository.InscripcionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY) // sustituye Oracle por H2 embebida
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop", // genera el esquema desde las entidades
        "spring.jpa.show-sql=false"
})
@Transactional // cada test hace rollback al terminar
class InscripcionServiceIT {

    @Autowired InscripcionService service;

    @Autowired EstudianteRepository estudianteRepo;
    @Autowired CursoRepository cursoRepo;
    @Autowired InscripcionRepository inscripcionRepo;

    // IDs generados que usaremos en las pruebas
    private Long inscripcionId;
    private Long estOldId, curOldId;
    private Long estNewId, curNewId;

    @BeforeEach
    void seed() {
        // Limpia en orden por FK
        inscripcionRepo.deleteAll();
        cursoRepo.deleteAll();
        estudianteRepo.deleteAll();

        // Crea "viejos"
        Estudiante estOld = new Estudiante();
        estOld.setNombre("Ana");                // ajusta más campos si tu entidad los requiere
        estOld = estudianteRepo.save(estOld);
        estOldId = estOld.getId();

        Curso curOld = new Curso();
        curOld.setNombre("Álgebra");
        curOld = cursoRepo.save(curOld);
        curOldId = curOld.getId();

        // Crea la inscripción base
        Inscripcion ins = new Inscripcion();
        ins.setEstudiante(estOld);
        ins.setCurso(curOld);
        ins.setPeriodo("2024-B");
        ins = inscripcionRepo.save(ins);
        inscripcionId = ins.getId();

        // Crea "nuevos" para el update
        Estudiante estNew = new Estudiante();
        estNew.setNombre("Luis");
        estNew = estudianteRepo.save(estNew);
        estNewId = estNew.getId();

        Curso curNew = new Curso();
        curNew.setNombre("Cálculo");
        curNew = cursoRepo.save(curNew);
        curNewId = curNew.getId();
    }

    @Test
    void actualizar_ok() {
        // Cambia estudiante, curso y periodo
     var in = new InscripcionIn(
    new IdRef(estNewId),
    new IdRef(curNewId),
    "2025-A"
);


        InscripcionDTO dto = service.actualizar(inscripcionId, in);

        assertEquals(inscripcionId, dto.id());
        assertEquals(estNewId, dto.estudiante().id());
        assertEquals(curNewId, dto.curso().id());
        assertEquals("2025-A", dto.periodo());
    }

    @Test
    void actualizar_cambiaSoloPeriodo_ok() {
        var in = new InscripcionIn(null, null, "2025-B");

        InscripcionDTO dto = service.actualizar(inscripcionId, in);

        assertEquals(estOldId, dto.estudiante().id()); // refs no cambian
        assertEquals(curOldId, dto.curso().id());
        assertEquals("2025-B", dto.periodo());
    }

    @Test
    void actualizar_fallaSiNoExisteEstudiante() {
        Long inexistente = 999L;
       var in = new InscripcionIn(new IdRef(inexistente), null, null);


        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class, () -> service.actualizar(inscripcionId, in));

        assertTrue(ex.getMessage().contains("Estudiante no encontrado: " + inexistente));
    }
}
