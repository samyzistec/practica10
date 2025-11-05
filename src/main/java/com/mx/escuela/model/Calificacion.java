package com.mx.escuela.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CALIFICACION")
@Getter @Setter @NoArgsConstructor
public class Calificacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_CALIFICACION")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSCRIPCION_ID") // FK real
  private Inscripcion inscripcion;

  @Column(name = "NOTA")
  private BigDecimal nota; // NUMBER(5,2)
}

