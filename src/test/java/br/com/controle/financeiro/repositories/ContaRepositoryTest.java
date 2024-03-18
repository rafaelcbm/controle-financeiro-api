package br.com.controle.financeiro.repositories;

import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.user.User;
import br.com.controle.financeiro.domain.user.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ContaRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    public void deveObterAsContasDoUsuario() {

        // Arrange
        String userLogin = "joao@teste.com";
        String userPassword = "senha_do_joao";
        var user = User.builder().login(userLogin).password(userPassword).role(UserRole.ADMIN).build();

        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).user(user).build();
        String nomeCartaoCredito = "Cartão Crédito";
        Conta contaCartaoCredito = Conta.builder().nome(nomeCartaoCredito).user(user).build();

        userRepository.save(user);
        contaRepository.save(contaCorrente);
        contaRepository.save(contaCartaoCredito);

        // Act
        List<Conta> contasUsuario = contaRepository.findAllContasByUserLogin(userLogin);

        // Assert
        Assertions.assertEquals(2, contasUsuario.size());
    }

    @Test
    public void deveCriarConta() {

        // Arrange
        String userLogin = "joao@teste.com";
        String userPassword = "senha_do_joao";
        var user = User.builder().login(userLogin).password(userPassword).role(UserRole.ADMIN).build();
        userRepository.save(user);

        Conta novaConta = Conta.builder().nome("Nova Conta").user(user).build();
        contaRepository.save(novaConta);

        // Act
        List<Conta> contas = contaRepository.findAll();

        // Assert
        Assertions.assertEquals(1, contas.size());
    }
}