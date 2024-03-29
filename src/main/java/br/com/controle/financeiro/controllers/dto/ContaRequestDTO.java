package br.com.controle.financeiro.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContaRequestDTO(String id,
                              @NotBlank(message = "Campo obrigatório não informado.")
                              String nome) {

}
