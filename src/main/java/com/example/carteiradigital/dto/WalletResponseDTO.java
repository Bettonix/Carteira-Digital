package com.example.carteiradigital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class WalletResponseDTO {
    private Long id;
    private BigDecimal saldo;
    private Long userId;
}

