package com.mx.escuela.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "INSCRIPCION")
@Getter @Setter @NoArgsConstructor
public class Inscripcion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_INSCRIPCION")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ESTUDIANTE_ID") // FK real
  private Estudiante estudiante;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CURSO_ID") // FK real
  private Curso curso;

  @Column(name = "PERIODO")
  private String periodo;
}
