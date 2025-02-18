package com.example.carteiradigital.repository;

import com.example.carteiradigital.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByOrigemIdOrDestinoId(Long origemId, Long destinoId);
}
