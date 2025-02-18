package com.example.carteiradigital.repository;

import com.example.carteiradigital.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOrigemIdOrDestinoId(Long origemId, Long destinoId);
}
