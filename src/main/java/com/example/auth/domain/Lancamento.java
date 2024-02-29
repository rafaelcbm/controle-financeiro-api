package com.example.auth.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nome;

    @JoinColumn(name = "conta_id")
    @ManyToOne
    private Conta conta;

    @JoinColumn(name = "categoria_id")
    @ManyToOne
    private Categoria categoria;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate data;

    @Column(precision = 8,scale = 2)
    private BigDecimal valor;

    @Column
    private boolean pago;
}
