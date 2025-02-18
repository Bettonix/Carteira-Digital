package com.example.carteiradigital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PixKeyResponseDTO {
    private String chave;
    private String tipo;
    private LocalDateTime criacao;
    private LocalDateTime expiracao;
}
