package com.example.carteiradigital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponseDTO {
    private String idTransacao;
    private String status;
    private BigDecimal valor;
    private String origem;
    private String destino;
    private LocalDateTime data;
}
