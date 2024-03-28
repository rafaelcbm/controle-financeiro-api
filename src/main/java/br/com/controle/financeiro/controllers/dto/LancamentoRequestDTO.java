package br.com.controle.financeiro.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoRequestDTO(String id,
                                   @NotNull(message = "Campo obrigatório não informado.")
                                   @NotBlank(message = "Campo obrigatório não deve ser vazio.")
                                   String nome,
                                   @NotNull(message = "Campo obrigatório não informado.")
                                   @NotBlank(message = "Campo obrigatório não deve ser vazio.")
                                   String idConta,
                                   @NotNull(message = "Campo obrigatório não informado.")
                                   @NotBlank(message = "Campo obrigatório não deve ser vazio.")
                                   String idCategoria,
                                   @NotNull(message = "Campo obrigatório não informado.")
                                   @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Data deve estar no formato: dd-MM-yyyy")
                                   String data,
                                   @NotNull(message = "Campo obrigatório não informado.") BigDecimal valor, boolean pago) {

    public LocalDate parseDate() {
        return LocalDate.parse(data, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
