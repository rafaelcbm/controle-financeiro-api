package br.com.controle.financeiro.repositories;

import br.com.controle.financeiro.domain.Categoria;
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
class CategoriaRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    public void deveObterAsCategoriasDoUsuario() {

        // Arrange
        String userLogin = "joao@teste.com";
        String userPassword = "senha_do_joao";
        var user = User.builder().login(userLogin).password(userPassword).role(UserRole.ADMIN).build();

        String nomeCategoriaCorrente = "Categoria Corrente";
        Categoria categoriaCorrente = Categoria.builder().nome(nomeCategoriaCorrente).user(user).build();
        String nomeCartaoCredito = "Cartão Crédito";
        Categoria categoriaCartaoCredito = Categoria.builder().nome(nomeCartaoCredito).user(user).build();

        userRepository.save(user);
        categoriaRepository.save(categoriaCorrente);
        categoriaRepository.save(categoriaCartaoCredito);

        // Act
        List<Categoria> categoriasUsuario = categoriaRepository.findAllCategoriasByUserLogin(userLogin);

        // Assert
        Assertions.assertEquals(2, categoriasUsuario.size());
    }

    @Test
    public void deveCriarCategoria() {

        // Arrange
        String userLogin = "joao@teste.com";
        String userPassword = "senha_do_joao";
        var user = User.builder().login(userLogin).password(userPassword).role(UserRole.ADMIN).build();
        userRepository.save(user);

        Categoria novaCategoria = Categoria.builder().nome("Nova Categoria").user(user).build();

        // Act
        categoriaRepository.save(novaCategoria);

        // Assert
        List<Categoria> categorias = categoriaRepository.findAll();
        Assertions.assertEquals(1, categorias.size());
    }
}