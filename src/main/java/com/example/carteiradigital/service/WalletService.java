package com.example.carteiradigital.service;

import com.example.carteiradigital.entity.Wallet;
import com.example.carteiradigital.exception.GlobalException;
import com.example.carteiradigital.repository.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public WalletService(WalletRepository walletRepository, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.walletRepository = walletRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<Wallet> getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId);
    }


    public Wallet consultarCarteira(Long userId) {
        String chaveCache = "wallet:" + userId;

        String walletJson = redisTemplate.opsForValue().get(chaveCache);
        if (walletJson != null) {
            try {
                return objectMapper.readValue(walletJson, Wallet.class);
            } catch (JsonProcessingException e) {
                throw new GlobalException("Erro ao processar dados do Redis.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new GlobalException("Carteira não encontrada!", HttpStatus.NOT_FOUND));

        try {
            String walletJsonCache = objectMapper.writeValueAsString(wallet);
            redisTemplate.opsForValue().set(chaveCache, walletJsonCache, 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new GlobalException("Erro ao salvar dados no Redis.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return wallet;
    }


    @Transactional
    public Wallet depositar(Long userId, BigDecimal valor) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new GlobalException("Carteira não encontrada!", HttpStatus.NOT_FOUND));

        wallet.setSaldo(wallet.getSaldo().add(valor));
        Wallet updatedWallet = walletRepository.save(wallet);

        atualizarCacheWallet(userId, updatedWallet);

        return updatedWallet;
    }

    @Transactional
    public Wallet sacar(Long userId, BigDecimal valor) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new GlobalException("Carteira não encontrada!", HttpStatus.NOT_FOUND));

        if (wallet.getSaldo().compareTo(valor) < 0) {
            throw new GlobalException("Saldo insuficiente!", HttpStatus.BAD_REQUEST);
        }

        wallet.setSaldo(wallet.getSaldo().subtract(valor));
        Wallet updatedWallet = walletRepository.save(wallet);

        atualizarCacheWallet(userId, updatedWallet);

        return updatedWallet;
    }

    private void atualizarCacheWallet(Long userId, Wallet wallet) {
        try {
            String walletJson = objectMapper.writeValueAsString(wallet);
            redisTemplate.opsForValue().set("wallet:" + userId, walletJson, 10, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            throw new GlobalException("Erro ao atualizar cache no Redis.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
