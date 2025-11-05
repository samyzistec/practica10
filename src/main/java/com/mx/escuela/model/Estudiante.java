package com.mx.escuela.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ESTUDIANTE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Estudiante {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_ESTUDIANTE")
  private Long id;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "FECHA_INGRESO")
  private java.sql.Date fechaIngreso;
}
