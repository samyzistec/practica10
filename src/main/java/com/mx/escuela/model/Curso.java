package com.mx.escuela.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CURSO")
@Getter @Setter @NoArgsConstructor
public class Curso {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CURSO")
  private Long id;

  @Column(name = "CLAVE", unique = true)
  private String clave;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "CREDITOS")
  private Integer creditos;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTRUCTOR_ID") // FK real
  private Instructor instructor;
}
