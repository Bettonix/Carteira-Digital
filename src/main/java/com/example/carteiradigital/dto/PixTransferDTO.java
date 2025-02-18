package com.example.carteiradigital.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PixTransferDTO {
    private Long origemId;
    private Long destinoId;
    private BigDecimal valor;
}