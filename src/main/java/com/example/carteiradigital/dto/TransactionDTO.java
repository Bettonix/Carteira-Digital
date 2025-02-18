package com.example.carteiradigital.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionDTO {

    @NotNull(message = "O ID da conta de origem é obrigatório.")
    private Long origemId;

    @NotNull(message = "O ID da conta de destino é obrigatório.")
    private Long destinoId;

    @NotNull(message = "O valor da transação é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor da transação deve ser maior que zero.")
    private BigDecimal valor;
}
