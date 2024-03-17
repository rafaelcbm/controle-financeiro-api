package br.com.controle.financeiro.services;

import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.UserRepository;
import br.com.controle.financeiro.services.exception.NegocioException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

        //Arrange
        String idConta = "123";
        String loginUsuario = "user@login.com";

        Conta contaEsperada = Conta.builder().nome("Cartão Crédito").build();
        Optional<Conta> contaEsperadaOpt = Optional.of(contaEsperada);
        Mockito.when(contaRepositoryMock.findById(idConta)).thenReturn(contaEsperadaOpt);

        //Act
        Conta contaResultado = contaService.obterContaPorId(idConta, loginUsuario);

        //Assert
        Assertions.assertEquals(contaEsperada, contaResultado);

        Mockito.verify(validacaoDadosUsuarioServiceMock)
                .validarContaDoUsuarioLogado(idConta, loginUsuario);
        Mockito.verify(contaRepositoryMock).findById(idConta);
    }

    @Test
    void naoDeveLancarErroAoValidarContaNomeDiferente() {

        //Arrange
        List<Conta> contas = List.of(Conta.builder().nome("Cartão Crédito").build());

        //Act
        contaService.validarContaComMesmoNome("Conta Conjunta", contas);

        //Assert
        //Não lançar erro.
    }

    @Test
    void deveLancarErroAoValidarContaMesmoNome() {

        //Arrange
        List<Conta> contas = List.of(Conta.builder().nome("Cartão Crédito").build());

        Assertions.assertThrows(
                //Assert
                NegocioException.class,
                //Act
                () -> contaService.validarContaComMesmoNome("Cartão Crédito", contas)
        );
    }

    @ParameterizedTest
    @MethodSource("provedorParametrosNomesContas")
    void naoDeveLancarErroAoValidarContasComNomesDiferentes(String nomeContaParametrizada, List<Conta> contasParametrizada) {

        // Assume que a lista de conta seja válida.
        // IGNORA o teste caso a lista de contas seja null, mas não considera uma FALHA.
        Assumptions.assumeTrue(contasParametrizada != null);

        contaService.validarContaComMesmoNome(nomeContaParametrizada, contasParametrizada);
    }

    static Stream<Arguments> provedorParametrosNomesContas() {

        List<Conta> contas = List.of(
                Conta.builder().nome("Conta Conjunta").build(),
                Conta.builder().nome("Cartão Crédito 1").build(),
                Conta.builder().nome("Cartão Crédito 2").build());

        return Stream.of(
                Arguments.of("Conta Conjunta 1", contas),
                Arguments.of("Cartão Crédito 3", contas),
                Arguments.of("Conta teste 3", null),
                Arguments.of("Cartão Crédito 4", contas)
        );
    }
}