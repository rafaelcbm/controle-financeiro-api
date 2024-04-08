package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.ContaRequestDTO;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.domain.user.UserRole;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContaServiceIntegrationTest {

    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ValidacaoDadosUsuarioService validacaoDadosUsuarioService;

    @Autowired
    private ContaService contaService;
    private Usuario usuarioPadrao = null;

    @BeforeEach
    public void prepararMassaTeste() {
        this.limparBase();
        this.criarUsuarioPadrao();
    }

    private void limparBase() {
        contaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    private void criarUsuarioPadrao() {
        String userLogin = "joao@teste.com";
        String userPassword = "senha_do_joao";
        usuarioPadrao = Usuario.builder().login(userLogin).password(userPassword).role(UserRole.ADMIN).build();
        usuarioRepository.save(usuarioPadrao);
    }

    @Test
    void deveObterContasDoUsuario() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();
        String nomeCartaoCredito = "Cartão Crédito";
        Conta contaCartaoCredito = Conta.builder().nome(nomeCartaoCredito).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        contaRepository.save(contaCartaoCredito);

        // Act
        List<Conta> contasUsuario = contaService.obterTodasContas(usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(2, contasUsuario.size());
    }

    @Test
    void deveObterContaPorId() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        List<Conta> contasUsuario = contaRepository.findAll();
        String idConta = contasUsuario.getFirst().getId();

        // Act
        Conta conta = contaService.obterContaPorId(idConta, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(idConta, conta.getId());
        Assertions.assertEquals(nomeContaCorrente, conta.getNome());
    }

    @Test
    void deveCriarConta() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        ContaRequestDTO novaConta = new ContaRequestDTO(null, nomeContaCorrente);

        // Act
        Conta conta = contaService.criarConta(novaConta, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertNotNull(conta.getId());
        Assertions.assertEquals(nomeContaCorrente, conta.getNome());
        Assertions.assertEquals(usuarioPadrao.getLogin(), conta.getUsuario().getLogin());
    }

    @Test
    void deveAtualizarConta() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);

        List<Conta> contas = contaRepository.findAll();
        String idConta = contas.getFirst().getId();
        String nomeContaAlterada = "Cartão de Crédito";
        ContaRequestDTO dadosContaAlterada = new ContaRequestDTO(idConta, nomeContaAlterada);

        // Act
        Conta contaAlterada = contaService.atualizarConta(idConta, dadosContaAlterada, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(idConta, contaAlterada.getId());
        Assertions.assertEquals(nomeContaAlterada, contaAlterada.getNome());
        Assertions.assertEquals(usuarioPadrao.getLogin(), contaAlterada.getUsuario().getLogin());
    }

    @Test
    void deveDeletarConta() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);

        List<Conta> contasAntesExclusao = contaRepository.findAll();

        //Assert
        //Verifica que a conta foi incluida
        Assertions.assertEquals(1, contasAntesExclusao.size());

        String idConta = contasAntesExclusao.getFirst().getId();

        // Act
        contaService.deletarConta(idConta, usuarioPadrao.getLogin());

        //Assert
        List<Conta> contasDepoisExclusao = contaRepository.findAll();
        Assertions.assertEquals(0, contasDepoisExclusao.size());
    }
}