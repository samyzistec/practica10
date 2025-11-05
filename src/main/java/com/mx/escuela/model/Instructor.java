package com.mx.escuela.model;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "INSTRUCTOR")
@Getter @Setter @NoArgsConstructor
public class Instructor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_INSTRUCTOR")
  private Long id;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "EMAIL")
  private String email;
}

