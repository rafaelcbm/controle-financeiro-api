package br.com.controle.financeiro.services;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;


@TestConfiguration
public class LancamentoServiceTestConfig {

    @Bean("valorMaximoLancamentoTeste")
    @Primary
    public BigDecimal valorMaximoLancamento() {
        // Altera o valor máximo para Um Milhão
        return BigDecimal.valueOf(1000000);
    }
}
