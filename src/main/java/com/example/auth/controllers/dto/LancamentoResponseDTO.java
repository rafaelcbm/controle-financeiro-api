package com.example.auth.controllers.dto;

import com.example.auth.domain.Lancamento;

import java.math.BigDecimal;

public record LancamentoResponseDTO(String id, String nome, String idConta,
                                    String idCategoria, String data, BigDecimal valor, boolean pago) {
    public LancamentoResponseDTO(Lancamento lancamento) {

        this(lancamento.getId(), lancamento.getNome(), lancamento.getConta().getId(),
                lancamento.getCategoria().getId(),
                lancamento.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                lancamento.getValor(), lancamento.isPago());

    }
}
