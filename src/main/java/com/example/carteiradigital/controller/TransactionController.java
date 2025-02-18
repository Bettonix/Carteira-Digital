package com.example.carteiradigital.controller;

import com.example.carteiradigital.dto.TransactionDTO;
import com.example.carteiradigital.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transacoes")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, String>> enviarTransacao(@RequestBody TransactionDTO dto) {
        String idTransacao = transactionService.enviarTransacao(dto);

        Map<String, String> response = new HashMap<>();
        response.put("id_transacao", idTransacao);
        response.put("mensagem", "Transação enviada para processamento!");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
