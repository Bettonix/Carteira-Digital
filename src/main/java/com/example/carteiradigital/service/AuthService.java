package com.example.carteiradigital.service;

import com.example.carteiradigital.dto.AuthDTO;
import com.example.carteiradigital.entity.User;
import com.example.carteiradigital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String cadastrarUsuario(AuthDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return "Usuário já cadastrado!";
        }
        User user = new User(dto.getEmail(), passwordEncoder.encode(dto.getSenha()));
        userRepository.save(user);
        return "Usuário cadastrado com sucesso!";
    }

    public Optional<User> autenticarUsuario(String email, String senha) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(senha, user.get().getSenha())) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<User> getUsuarioPorEmail(String email) {
        return userRepository.findByEmail(email);
    }
}

