package com.mx.escuela.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.escuela.dto.IdRef;
import com.mx.escuela.dto.InscripcionDTO;
import com.mx.escuela.dto.InscripcionIn;
import com.mx.escuela.dto.MiniRef;
import com.mx.escuela.service.InscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InscripcionController.class)
class InscripcionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private InscripcionService service;

    @BeforeEach void setup() { objectMapper.findAndRegisterModules(); }

    private InscripcionDTO dto(Long id, Long estId, String estNom, Long cursoId, String cursoNom, String periodo) {
        return new InscripcionDTO(id, new MiniRef(estId, estNom), new MiniRef(cursoId, cursoNom), periodo);
    }
    private InscripcionIn in(Long estId, Long cursoId, String periodo) {
        return new InscripcionIn(new IdRef(estId), new IdRef(cursoId), periodo);
    }

    @Test
    void listar_ok() throws Exception {
        var d1 = dto(1L, 10L, "Ana Pérez", 20L, "Bases de Datos", "2024-01");
        var d2 = dto(2L, 11L, "Luis Gómez", 21L, "Programación", "2024-01");
        Mockito.when(service.listar()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/inscripciones"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].estudiante.nombre").value("Ana Pérez"))
               .andExpect(jsonPath("$[1].curso.nombre").value("Programación"));
    }

    @Test
    void buscar_ok() throws Exception {
        var d = dto(5L, 10L, "Ana Pérez", 20L, "Bases de Datos", "2024-01");
        Mockito.when(service.buscar(5L)).thenReturn(d);

        mockMvc.perform(get("/api/inscripciones/{id}", 5L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(5))
               .andExpect(jsonPath("$.curso.nombre").value("Bases de Datos"));
    }

    @Test
    void crear_created_Location() throws Exception {
        var input = in(10L, 20L, "2024-01");
        var out = dto(99L, 10L, "Ana Pérez", 20L, "Bases de Datos", "2024-01");
        Mockito.when(service.crear(any(InscripcionIn.class))).thenReturn(out);

        mockMvc.perform(post("/api/inscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", "/api/inscripciones/99"))
               .andExpect(jsonPath("$.estudiante.id").value(10))
               .andExpect(jsonPath("$.curso.id").value(20));
    }

    @Test
    void actualizar_ok() throws Exception {
        var input = in(11L, 22L, "2024-02");
        var out = dto(7L, 11L, "Luis Gómez", 22L, "Estructuras", "2024-02");
        Mockito.when(service.actualizar(eq(7L), any(InscripcionIn.class))).thenReturn(out);

        mockMvc.perform(put("/api/inscripciones/{id}", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(7))
               .andExpect(jsonPath("$.periodo").value("2024-02"));
    }

    @Test
    void eliminar_noContent() throws Exception {
        Mockito.doNothing().when(service).eliminar(8L);

        mockMvc.perform(delete("/api/inscripciones/{id}", 8L))
               .andExpect(status().isNoContent());
    }
}
