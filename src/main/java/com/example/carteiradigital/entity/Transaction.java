package com.example.carteiradigital.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long origemId;

    @Column(nullable = false)
    private Long destinoId;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private LocalDateTime data;

    public Transaction(Long origemId, Long destinoId, double valor) {
        this.origemId = origemId;
        this.destinoId = destinoId;
        this.valor = valor;
        this.data = LocalDateTime.now();
    }
}
