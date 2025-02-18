package com.example.carteiradigital.service;

import com.example.carteiradigital.dto.TransactionDTO;
import com.example.carteiradigital.entity.Transaction;
import com.example.carteiradigital.exception.GlobalException;
import com.example.carteiradigital.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TransactionRepository transactionRepository;

    public TransactionService(KafkaTemplate<String, String> kafkaTemplate, TransactionRepository transactionRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction enviarTransacao(TransactionDTO dto) {
        if (dto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalException("O valor da transação deve ser maior que zero!", HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .origem(dto.getOrigemId())
                .destino(dto.getDestinoId())
                .valor(dto.getValor())
                .status("PENDENTE")
                .dataCriacao(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);

        String mensagem = String.format("Transação %s de %s para %s no valor de R$ %.2f",
                transaction.getId(), dto.getOrigemId(), dto.getDestinoId(), dto.getValor());

        kafkaTemplate.send("transacoes", mensagem);

        logger.info("Transação registrada no banco e enviada ao Kafka: {}", mensagem);

        return transaction;
    }

}
