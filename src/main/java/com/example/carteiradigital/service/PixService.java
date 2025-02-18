package com.example.carteiradigital.service;

import com.example.carteiradigital.dto.PixResponseDTO;
import com.example.carteiradigital.dto.PixTransferDTO;
import com.example.carteiradigital.entity.PixKey;
import com.example.carteiradigital.entity.Wallet;
import com.example.carteiradigital.exception.GlobalException;
import com.example.carteiradigital.repository.PixKeyRepository;
import com.example.carteiradigital.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PixService {

    private static final Logger logger = LoggerFactory.getLogger(PixService.class);
    private final WalletRepository walletRepository;
    private final PixKeyRepository pixKeyRepository;

    public PixService(WalletRepository walletRepository, PixKeyRepository pixKeyRepository) {
        this.walletRepository = walletRepository;
        this.pixKeyRepository = pixKeyRepository;
    }

    @Transactional
    public PixResponseDTO transferirPix(PixTransferDTO dto) {
        Optional<Wallet> origemWalletOpt = walletRepository.findByUserId(dto.getOrigemId());
        Optional<Wallet> destinoWalletOpt = walletRepository.findByUserId(dto.getDestinoId());

        if (origemWalletOpt.isEmpty() || destinoWalletOpt.isEmpty()) {
            throw new GlobalException("Carteira não encontrada!", HttpStatus.NOT_FOUND);
        }

        Wallet origem = origemWalletOpt.get();
        Wallet destino = destinoWalletOpt.get();

        if (origem.getSaldo().compareTo(dto.getValor()) < 0) {
            throw new GlobalException("Saldo insuficiente!", HttpStatus.BAD_REQUEST);
        }

        origem.setSaldo(origem.getSaldo().subtract(dto.getValor()));
        destino.setSaldo(destino.getSaldo().add(dto.getValor()));

        walletRepository.save(origem);
        walletRepository.save(destino);

        String idTransacao = UUID.randomUUID().toString();
        LocalDateTime dataTransacao = LocalDateTime.now();

        logger.info("PIX realizado: ID {}, Origem {}, Destino {}, Valor {}",
                idTransacao, origem.getUserId(), destino.getUserId(), dto.getValor());

        return new PixResponseDTO(idTransacao, "CONFIRMADO", dto.getValor(),
                origem.getUserId().toString(), destino.getUserId().toString(), dataTransacao);
    }

    public PixKey gerarChavePix(Long userId) {

        Optional<PixKey> chaveExistente = pixKeyRepository.findByUserId(userId);
        if (chaveExistente.isPresent() && chaveExistente.get().getDataExpiracao().isAfter(LocalDateTime.now())) {
            return chaveExistente.get();
        }

        String chavePix = "pix-" + UUID.randomUUID();
        LocalDateTime dataCriacao = LocalDateTime.now();
        LocalDateTime dataExpiracao = dataCriacao.plusMinutes(30);

        PixKey novaChave = new PixKey(chavePix, userId, dataCriacao, dataExpiracao);
        pixKeyRepository.save(novaChave);

        logger.info("Chave PIX gerada para o usuário {}: {}", userId, chavePix);

        return novaChave;
    }
}

