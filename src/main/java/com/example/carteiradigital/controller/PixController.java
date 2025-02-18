package com.example.carteiradigital.controller;

import com.example.carteiradigital.dto.PixResponseDTO;
import com.example.carteiradigital.dto.PixTransferDTO;
import com.example.carteiradigital.dto.UserResponseDTO;
import com.example.carteiradigital.entity.PixKey;
import com.example.carteiradigital.entity.User;
import com.example.carteiradigital.service.AuthService;
import com.example.carteiradigital.service.JwtService;
import com.example.carteiradigital.service.PixService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/pix")
public class PixController {

    private final PixService pixService;
    private final JwtService jwtService;
    private final AuthService authService;

    public PixController(PixService pixService, JwtService jwtService, AuthService authService) {
        this.pixService = pixService;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/gerar-chave")
    public ResponseEntity<Map<String, Object>> gerarChavePix(@RequestHeader("Authorization") String token) {


        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<User> user = authService.getUsuarioPorEmail(email);

            PixKey chavePix = pixService.gerarChavePix(user.get().getId());


        Map<String, Object> response = new HashMap<>();
        response.put("chave", chavePix.getChave());
        response.put("tipo", "aleatória");
        response.put("criacao", chavePix.getDataCriacao().toString());
        response.put("expiracao", chavePix.getDataExpiracao().toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferirPix(@RequestBody PixTransferDTO dto) {
        try {
            PixResponseDTO resposta = pixService.transferirPix(dto);
            return ResponseEntity.ok(resposta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }


}
