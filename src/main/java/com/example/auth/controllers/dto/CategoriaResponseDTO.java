package com.example.auth.controllers.dto;

import com.example.auth.domain.Categoria;

public record CategoriaResponseDTO(String id, String nome) {
    public CategoriaResponseDTO(Categoria categoria) {
        this(categoria.getId(), categoria.getNome());
    }
}
