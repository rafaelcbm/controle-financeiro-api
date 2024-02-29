package com.example.auth.controllers.dto;

import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequestDTO(String id, String nome, String idConta, String idCategoria,
                                   @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Data deve estar no formato: dd-MM-yyyy")
                                   String data, BigDecimal valor, boolean pago) {

    public LocalDate parseDate() {
        return LocalDate.parse(data, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
