package com.mx.escuela.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.escuela.model.Estudiante;
import com.mx.escuela.service.EstudianteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;                // <-- usar java.sql.Date para alinear con la entidad
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EstudianteController.class)
class EstudianteControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private EstudianteService service;

    private Estudiante est(Long id, String nombre, String email, String fechaISO) {
        Estudiante e = new Estudiante();
        e.setId(id);
        e.setNombre(nombre);
        e.setEmail(email);
        e.setFechaIngreso(Date.valueOf(fechaISO)); // <-- Date.valueOf("yyyy-MM-dd")
        return e;
    }

    @Test
    void listar_ok() throws Exception {
        var e1 = est(1L, "Ana Pérez", "ana.perez@email.com", "2023-08-15");
        var e2 = est(2L, "Luis Gómez", "luis.gomez@email.com", "2023-08-20");
        Mockito.when(service.listar()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Ana Pérez"))
                .andExpect(jsonPath("$[1].email").value("luis.gomez@email.com"));
    }

    @Test
    void buscar_ok() throws Exception {
        var e = est(1L, "Ana Pérez", "ana.perez@email.com", "2023-08-15");
        Mockito.when(service.buscar(1L)).thenReturn(e);

        mockMvc.perform(get("/api/estudiantes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Ana Pérez"));
    }

    @Test
    void crear_created_con_Location() throws Exception {
        var body  = est(null, "Nuevo", "nuevo@email.com", "2024-01-01");
        var saved = est(99L, "Nuevo", "nuevo@email.com", "2024-01-01");
        Mockito.when(service.crear(any(Estudiante.class))).thenReturn(saved);

        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/estudiantes/99"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.nombre").value("Nuevo"));
    }

    @Test
    void actualizar_ok() throws Exception {
        var body    = est(null, "Nombre Mod", "mod@email.com", "2024-02-02");
        var updated = est(5L,   "Nombre Mod", "mod@email.com", "2024-02-02");
        Mockito.when(service.actualizar(eq(5L), any(Estudiante.class))).thenReturn(updated);

        mockMvc.perform(put("/api/estudiantes/{id}", 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Nombre Mod"));
    }

    @Test
    void eliminar_noContent() throws Exception {
        Mockito.doNothing().when(service).eliminar(7L);

        mockMvc.perform(delete("/api/estudiantes/{id}", 7L))
                .andExpect(status().isNoContent());
    }
}
