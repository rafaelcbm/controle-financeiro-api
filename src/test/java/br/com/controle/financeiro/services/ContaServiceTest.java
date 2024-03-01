package br.com.controle.financeiro.services;

import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    ContaRepository contaRepositoryMock;
    @Mock
    UserRepository userRepositoryMock;
    @Mock
    ValidacaoDadosUsuarioService validacaoDadosUsuarioServiceMock;

    @InjectMocks
    ContaService contaService;

    @Test
    void obterContaPorId() {

        String idConta = "123";
        String loginUsuario = "user@login.com";

        Conta contaEsperada = Conta.builder().nome("Cartão Crédito").build();
        Optional<Conta> contaEsperadaOpt = Optional.of(contaEsperada);
        Mockito.when(contaRepositoryMock.findById(idConta)).thenReturn(contaEsperadaOpt);

        Conta contaResultado = contaService.obterContaPorId(idConta, loginUsuario);
        Assertions.assertEquals(contaEsperada, contaResultado);

        Mockito.verify(validacaoDadosUsuarioServiceMock)
                .validarContaDoUsuarioLogado(idConta, loginUsuario);
        Mockito.verify(contaRepositoryMock).findById(idConta);
    }
}