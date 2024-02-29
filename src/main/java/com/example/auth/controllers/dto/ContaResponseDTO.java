package com.example.auth.controllers.dto;

import com.example.auth.domain.Conta;

public record ContaResponseDTO(String id, String nome) {
    public ContaResponseDTO(Conta conta) {
        this(conta.getId(), conta.getNome());
    }
}
