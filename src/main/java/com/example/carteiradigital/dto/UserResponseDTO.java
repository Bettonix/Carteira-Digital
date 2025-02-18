package com.example.carteiradigital.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDTO {
    private String mensagem;
    private String token;

    public UserResponseDTO(String mensagem) {
        this.mensagem = mensagem;
    }
}