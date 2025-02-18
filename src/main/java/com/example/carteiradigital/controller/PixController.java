package com.example.carteiradigital.controller;

import com.example.carteiradigital.dto.PixKeyResponseDTO;
import com.example.carteiradigital.dto.PixResponseDTO;
import com.example.carteiradigital.dto.PixTransferDTO;
import com.example.carteiradigital.entity.PixKey;
import com.example.carteiradigital.entity.User;
import com.example.carteiradigital.exception.GlobalException;
import com.example.carteiradigital.service.AuthService;
import com.example.carteiradigital.service.JwtService;
import com.example.carteiradigital.service.PixService;
import com.example.carteiradigital.service.RateLimitingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pix")
public class PixController {

    private final PixService pixService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final RateLimitingService rateLimitingService;

    public PixController(PixService pixService, JwtService jwtService, AuthService authService, RateLimitingService rateLimitingService) {
        this.pixService = pixService;
        this.jwtService = jwtService;
        this.authService = authService;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/gerar-chave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PixKeyResponseDTO> gerarChavePix(@RequestHeader("Authorization") String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        User user = authService.getUsuarioPorEmail(email)
                .orElseThrow(() -> new GlobalException("Usuário não encontrado!", HttpStatus.UNAUTHORIZED));

        PixKey chavePix = pixService.gerarChavePix(user.getId());

        PixKeyResponseDTO response = new PixKeyResponseDTO(
                chavePix.getChave(),
                "aleatória",
                chavePix.getDataCriacao(),
                chavePix.getDataExpiracao()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/transferir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PixResponseDTO> transferirPix(@RequestHeader("Authorization") String token, @RequestBody PixTransferDTO dto) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        User user = authService.getUsuarioPorEmail(email)
                .orElseThrow(() -> new GlobalException("Usuário não encontrado!", HttpStatus.UNAUTHORIZED));

        if (!rateLimitingService.podeRealizarPix(user.getId().toString())) {
            throw new GlobalException("Muitas transações em pouco tempo, tente novamente mais tarde.", HttpStatus.TOO_MANY_REQUESTS);
        }

        if (!user.getId().equals(dto.getOrigemId())) {
            throw new GlobalException("Usuário não autorizado para esta transação!", HttpStatus.FORBIDDEN);
        }

        PixResponseDTO resposta = pixService.transferirPix(dto);
        return ResponseEntity.ok(resposta);
    }
}
