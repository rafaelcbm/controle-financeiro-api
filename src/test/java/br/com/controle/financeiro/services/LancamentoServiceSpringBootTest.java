package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.LancamentoRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.domain.user.User;
import br.com.controle.financeiro.repositories.LancamentoRepository;
import br.com.controle.financeiro.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ContextConfiguration(classes = {LancamentoServiceTestConfig.class})
@SpringBootTest
@ExtendWith(SpringExtension.class)
class LancamentoServiceSpringBootTest {

    @MockBean
    LancamentoRepository lancamentoRepositoryMock;
    @MockBean
    UserRepository userRepositoryMock;
    @MockBean
    ValidacaoDadosUsuarioService validacaoDadosUsuarioServiceMock;

    @Autowired
    BigDecimal valorMaximoLancamento;

    @Autowired
    LancamentoService lancamentoService;

    @Test
    void deveObterLancamentoPorId() {

        //Arrange
        String idLancamento = "123";
        String loginUsuario = "user@login.com";

        Lancamento lancamentoEsperada = Lancamento.builder().nome("Pizza").build();
        Optional<Lancamento> lancamentoEsperadaOpt = Optional.of(lancamentoEsperada);
        Mockito.when(lancamentoRepositoryMock.findById(idLancamento)).thenReturn(lancamentoEsperadaOpt);

        //Act
        Lancamento lancamentoResultado = lancamentoService.obterLancamentoPorId(idLancamento, loginUsuario);

        //Assert
        Assertions.assertEquals(lancamentoEsperada, lancamentoResultado);

        Mockito.verify(validacaoDadosUsuarioServiceMock)
                .validarLancamentoDoUsuarioLogado(idLancamento, loginUsuario);
        Mockito.verify(lancamentoRepositoryMock).findById(idLancamento);
    }

    @Test
    void deveCriarLancamento() {

        //Arrange
        String loginUsuario = "user@login.com";
        String nomeNovoLancamento = "Pizza";

        String idContaFake = "id_conta_fake";
        String idCategoriaFake = "id_categoria_fake";
        String dataLancamento = "03-05-2024";
        BigDecimal valorLancamento = BigDecimal.valueOf(1000000);
        LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(null, nomeNovoLancamento,
                idContaFake, idCategoriaFake,
                dataLancamento, valorLancamento, false);

        List<Lancamento> lancamentosExistentes = List.of(Lancamento.builder().nome("Cartão Crédito").build());
        Mockito.when(lancamentoRepositoryMock.findLancamentosByUser(loginUsuario)).thenReturn(lancamentosExistentes);

        User usuario = User.builder().login(loginUsuario).id("1234").build();
        Mockito.when(userRepositoryMock.findByLogin(loginUsuario)).thenReturn(usuario);

        Lancamento lancamentoEsperado = Lancamento.builder()
                .nome(nomeNovoLancamento)
                .valor(valorLancamento)
                .data(LocalDate.parse(dataLancamento, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .pago(false)
                .conta(Conta.builder().nome("Cartão Crédito").user(usuario).build())
                .categoria(Categoria.builder().nome("Alimentação").user(usuario).build())
                .build();
        Mockito.when(lancamentoRepositoryMock.save(Mockito.any(Lancamento.class))).thenReturn(lancamentoEsperado);

        //Act
        Lancamento lancamentoResultado = lancamentoService.criarLancamento(novoLancamentoDto, loginUsuario);

        //Assert
        Assertions.assertEquals(lancamentoEsperado, lancamentoResultado);
    }

}
