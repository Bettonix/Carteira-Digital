package com.example.carteiradigital.controller;

import com.example.carteiradigital.dto.WalletResponseDTO;
import com.example.carteiradigital.entity.Wallet;
import com.example.carteiradigital.exception.GlobalException;
import com.example.carteiradigital.service.WalletService;
import com.example.carteiradigital.service.JwtService;
import com.example.carteiradigital.service.AuthService;
import com.example.carteiradigital.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/carteira")
public class WalletController {

    private final WalletService walletService;
    private final JwtService jwtService;
    private final AuthService authService;

    public WalletController(WalletService walletService, JwtService jwtService, AuthService authService) {
        this.walletService = walletService;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @GetMapping("/saldo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponseDTO> consultarSaldo(@RequestHeader("Authorization") String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        User user = authService.getUsuarioPorEmail(email)
                .orElseThrow(() -> new GlobalException("Usuário não encontrado!", HttpStatus.UNAUTHORIZED));

        Wallet wallet = walletService.consultarCarteira(user.getId());

        WalletResponseDTO response = new WalletResponseDTO(
                wallet.getId(),
                wallet.getSaldo(),
                wallet.getUserId()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/depositar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponseDTO> depositar(@RequestHeader("Authorization") String token, @RequestParam BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalException("O valor do depósito deve ser maior que zero!", HttpStatus.BAD_REQUEST);
        }

        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        User user = authService.getUsuarioPorEmail(email)
                .orElseThrow(() -> new GlobalException("Usuário não encontrado!", HttpStatus.UNAUTHORIZED));

        Wallet wallet = walletService.depositar(user.getId(), valor);

        WalletResponseDTO response = new WalletResponseDTO(
                wallet.getId(),
                wallet.getSaldo(),
                wallet.getUserId()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sacar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WalletResponseDTO> sacar(@RequestHeader("Authorization") String token, @RequestParam BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new GlobalException("O valor do saque deve ser maior que zero!", HttpStatus.BAD_REQUEST);
        }

        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        User user = authService.getUsuarioPorEmail(email)
                .orElseThrow(() -> new GlobalException("Usuário não encontrado!", HttpStatus.UNAUTHORIZED));

        Wallet wallet = walletService.getWalletByUserId(user.getId())
                .orElseThrow(() -> new GlobalException("Carteira não encontrada!", HttpStatus.NOT_FOUND));

        if (wallet.getSaldo().compareTo(valor) < 0) {
            throw new GlobalException("Saldo insuficiente para saque!", HttpStatus.BAD_REQUEST);
        }

        wallet = walletService.sacar(user.getId(), valor);

        WalletResponseDTO response = new WalletResponseDTO(
                wallet.getId(),
                wallet.getSaldo(),
                wallet.getUserId()
        );

        return ResponseEntity.ok(response);
    }
}
