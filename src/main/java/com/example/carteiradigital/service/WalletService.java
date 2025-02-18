package com.example.carteiradigital.service;

import com.example.carteiradigital.entity.Wallet;
import com.example.carteiradigital.repository.WalletRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public double consultarSaldo(Long userId) {
        return walletRepository.findByUserId(userId)
                .map(Wallet::getSaldo)
                .orElse(0.0);
    }

    public String depositar(Long userId, double valor) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElse(new Wallet(userId, 0.0));
        wallet.setSaldo(wallet.getSaldo() + valor);
        walletRepository.save(wallet);
        return "Depósito realizado!";
    }

    public String sacar(Long userId, double valor) {
        Optional<Wallet> walletOpt = walletRepository.findByUserId(userId);
        if (walletOpt.isPresent()) {
            Wallet wallet = walletOpt.get();
            if (wallet.getSaldo() >= valor) {
                wallet.setSaldo(wallet.getSaldo() - valor);
                walletRepository.save(wallet);
                return "Saque realizado!";
            }
            return "Saldo insuficiente!";
        }
        return "Carteira não encontrada!";
    }
}
