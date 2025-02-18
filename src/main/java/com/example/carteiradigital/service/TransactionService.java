package com.example.carteiradigital.service;

import com.example.carteiradigital.dto.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransactionService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String enviarTransacao(TransactionDTO dto) {
        String idTransacao = UUID.randomUUID().toString();
        String mensagem = String.format("Transação %s de %s para %s no valor de R$ %.2f",
                idTransacao, dto.getOrigem(), dto.getDestino(), dto.getValor());

        kafkaTemplate.send("transacoes", mensagem);

        logger.info("Transação enviada: {}", mensagem);

        return idTransacao;
    }
}
