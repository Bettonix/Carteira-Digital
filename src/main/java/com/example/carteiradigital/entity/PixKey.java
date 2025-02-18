package com.example.carteiradigital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pix_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PixKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String chave;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataExpiracao;

    public PixKey(String chave, Long userId, LocalDateTime dataCriacao, LocalDateTime dataExpiracao) {
        this.chave = chave;
        this.userId = userId;
        this.dataCriacao = dataCriacao;
        this.dataExpiracao = dataExpiracao;
    }
}
