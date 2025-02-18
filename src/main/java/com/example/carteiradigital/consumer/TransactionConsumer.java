package com.example.carteiradigital.consumer;

import com.example.carteiradigital.entity.Transaction;
import com.example.carteiradigital.exception.GlobalException;
import com.example.carteiradigital.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);
    private final TransactionRepository transactionRepository;

    public TransactionConsumer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = "transacoes", groupId = "grupo-transacoes")
    @Transactional
    public void processarTransacao(String mensagem) {
        logger.info("Mensagem recebida do Kafka: {}", mensagem);

        try {
            UUID idTransacao = extrairIdTransacao(mensagem);

            Optional<Transaction> transacaoOpt = transactionRepository.findById(idTransacao);

            if (transacaoOpt.isEmpty()) {
                throw new GlobalException("Transação não encontrada no banco!", HttpStatus.NOT_FOUND);
            }

            Transaction transacao = transacaoOpt.get();

            if ("CONFIRMADO".equals(transacao.getStatus())) {
                logger.warn("Transação {} já foi confirmada anteriormente.", idTransacao);
                return;
            }

            transacao.setStatus("CONFIRMADO");
            transactionRepository.save(transacao);

            logger.info("Transação {} confirmada com sucesso.", idTransacao);

        } catch (Exception e) {
            logger.error("Erro ao processar transação: {}", e.getMessage());
        }
    }

    private UUID extrairIdTransacao(String mensagem) {
        try {
            String[] partes = mensagem.split(" ");
            return UUID.fromString(partes[1]);
        } catch (Exception e) {
            throw new GlobalException("Falha ao extrair ID da transação da mensagem Kafka.", HttpStatus.BAD_REQUEST);
        }
    }
}
