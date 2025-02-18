package com.example.carteiradigital.controller;

import com.example.carteiradigital.dto.TransactionDTO;
import com.example.carteiradigital.dto.TransactionResponseDTO;
import com.example.carteiradigital.entity.Transaction;
import com.example.carteiradigital.exception.GlobalException;
import com.example.carteiradigital.service.RateLimitingService;
import com.example.carteiradigital.service.TransactionService;
import com.example.carteiradigital.service.JwtService;
import com.example.carteiradigital.service.AuthService;
import com.example.carteiradigital.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacoes")
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final RateLimitingService rateLimitingService;

    public TransactionController(TransactionService transactionService, JwtService jwtService, AuthService authService, RateLimitingService rateLimitingService) {
        this.transactionService = transactionService;
        this.jwtService = jwtService;
        this.authService = authService;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/enviar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionResponseDTO> enviarTransacao(
            @RequestHeader("Authorization") String token,
            @RequestBody TransactionDTO dto) {

        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        User user = authService.getUsuarioPorEmail(email)
                .orElseThrow(() -> new GlobalException("Usuário não encontrado!", HttpStatus.UNAUTHORIZED));

        if (!user.getId().equals(dto.getOrigemId())) {
            throw new GlobalException("Usuário não autorizado para esta transação!", HttpStatus.FORBIDDEN);
        }

        if (!rateLimitingService.podeRealizarTransacao(user.getId().toString())) {
            throw new GlobalException("Muitas transações em pouco tempo, tente novamente mais tarde.", HttpStatus.TOO_MANY_REQUESTS);
        }

        Transaction transaction = transactionService.enviarTransacao(dto);

        TransactionResponseDTO response = new TransactionResponseDTO(
                transaction.getId().toString(),
                transaction.getStatus(),
                transaction.getValor(),
                transaction.getOrigem().toString(),
                transaction.getDestino().toString(),
                transaction.getDataCriacao()
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
