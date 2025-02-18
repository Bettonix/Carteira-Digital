package com.example.carteiradigital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PixResponseDTO {
    private String idTransacao;
    private String status;
    private Double valor;
    private String origem;
    private String destino;
    private LocalDateTime data;
}
