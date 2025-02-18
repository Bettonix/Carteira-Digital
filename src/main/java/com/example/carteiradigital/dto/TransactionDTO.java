package com.example.carteiradigital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {
    private String origem;
    private String destino;
    private double valor;
}