package com.example.carteiradigital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PixTransferDTO {
    private Long origemId;
    private Long destinoId;
    private double valor;
}