package com.example.carteiradigital.controller;

import com.example.carteiradigital.dto.AuthDTO;
import com.example.carteiradigital.dto.LoginDTO;
import com.example.carteiradigital.dto.UserResponseDTO;
import com.example.carteiradigital.entity.User;
import com.example.carteiradigital.exception.GlobalException;
import com.example.carteiradigital.service.AuthService;
import com.example.carteiradigital.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UserResponseDTO> cadastrar(@RequestBody AuthDTO dto) {
        String resultado = authService.cadastrarUsuario(dto);

        if (resultado.equals("Usuário já cadastrado!")) {
            throw new GlobalException(resultado, HttpStatus.CONFLICT);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponseDTO(resultado));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginDTO dto) {
        Optional<User> user = authService.autenticarUsuario(dto.getEmail(), dto.getSenha());

        if (user.isPresent()) {
            String token = jwtService.generateToken(dto.getEmail());
            return ResponseEntity.ok(new UserResponseDTO("Autenticado com sucesso!", token));
        }

        throw new GlobalException("Credenciais inválidas!", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> perfil(@RequestHeader("Authorization") String token) {
        String email = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<User> user = authService.getUsuarioPorEmail(email);

        return user.map(value -> ResponseEntity.ok(new UserResponseDTO("Usuário encontrado!", value.getEmail())))
                .orElseThrow(() -> new GlobalException("Usuário não encontrado!", HttpStatus.NOT_FOUND));
    }
}
