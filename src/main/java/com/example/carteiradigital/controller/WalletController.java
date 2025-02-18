package com.example.carteiradigital.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/carteira")
public class WalletController {

    private double saldo = 1000.00; // Simulação de saldo inicial

    @GetMapping("/saldo")
    public Map<String, Double> saldo() {
        Map<String, Double> response = new HashMap<>();
        response.put("saldo", saldo);
        return response;
    }

    @PostMapping("/deposito")
    public Map<String, Object> deposito(@RequestParam double valor) {
        saldo += valor;
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Depósito realizado com sucesso!");
        response.put("novo_saldo", saldo);
        return response;
    }

    @PostMapping("/saque")
    public Map<String, Object> saque(@RequestParam double valor) {
        Map<String, Object> response = new HashMap<>();
        if (valor > saldo) {
            response.put("mensagem", "Saldo insuficiente!");
        } else {
            saldo -= valor;
            response.put("mensagem", "Saque realizado com sucesso!");
            response.put("novo_saldo", saldo);
        }
        return response;
    }
}
