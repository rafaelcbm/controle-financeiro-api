package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.CategoriaRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.domain.user.UserRole;
import br.com.controle.financeiro.repositories.CategoriaRepository;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.LancamentoRepository;
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
class CategoriaServiceIntegrationTest {


    @Autowired
    private LancamentoRepository lancamentoRepository;
    @Autowired
    private ContaRepository contaRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaService categoriaService;
    private Usuario usuarioPadrao = null;

    @BeforeEach
    public void prepararMassaTeste() {
        this.limparBase();
        this.criarUsuarioPadrao();
    }

    private void limparBase() {
        lancamentoRepository.deleteAll();
        categoriaRepository.deleteAll();
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
    void deveObterCategoriasDoUsuario() {

        //Arrange
        String nomeCategoriaTransporte = "Transporte";
        Categoria categoriaTransporte = Categoria.builder().nome(nomeCategoriaTransporte).usuario(usuarioPadrao).build();
        String nomeCategoriaAlimentacao = "Alimentacao";
        Categoria categoriaCartaoCredito = Categoria.builder().nome(nomeCategoriaAlimentacao).usuario(usuarioPadrao).build();

        categoriaRepository.save(categoriaTransporte);
        categoriaRepository.save(categoriaCartaoCredito);

        // Act
        List<Categoria> categoriasUsuario = categoriaService.obterTodasCategorias(usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(2, categoriasUsuario.size());
    }

    @Test
    void deveObterCategoriaPorId() {

        //Arrange
        String nomeCategoriaTransporte = "Transporte";
        Categoria categoriaTransporte = Categoria.builder().nome(nomeCategoriaTransporte).usuario(usuarioPadrao).build();

        categoriaRepository.save(categoriaTransporte);
        List<Categoria> categoriasUsuario = categoriaRepository.findAll();
        String idCategoria = categoriasUsuario.getFirst().getId();

        // Act
        Categoria categoria = categoriaService.obterCategoriaPorId(idCategoria, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(idCategoria, categoria.getId());
        Assertions.assertEquals(nomeCategoriaTransporte, categoria.getNome());
    }

    @Test
    void deveCriarCategoria() {

        //Arrange
        String nomeCategoriaTransporte = "Transporte";
        CategoriaRequestDTO novaCategoria = new CategoriaRequestDTO(null, nomeCategoriaTransporte);

        // Act
        Categoria categoria = categoriaService.criarCategoria(novaCategoria, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertNotNull(categoria.getId());
        Assertions.assertEquals(nomeCategoriaTransporte, categoria.getNome());
        Assertions.assertEquals(usuarioPadrao.getLogin(), categoria.getUsuario().getLogin());
    }

    @Test
    void deveAtualizarCategoria() {

        //Arrange
        String nomeCategoriaTransporte = "Transporte";
        Categoria categoriaTransporte = Categoria.builder().nome(nomeCategoriaTransporte).usuario(usuarioPadrao).build();

        categoriaRepository.save(categoriaTransporte);

        List<Categoria> categorias = categoriaRepository.findAll();
        String idCategoria = categorias.getFirst().getId();
        String nomeCategoriaAlterada = "Alimentação";
        CategoriaRequestDTO dadosCategoriaAlterada = new CategoriaRequestDTO(idCategoria, nomeCategoriaAlterada);

        // Act
        Categoria categoriaAlterada = categoriaService.atualizarCategoria(idCategoria, dadosCategoriaAlterada, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(idCategoria, categoriaAlterada.getId());
        Assertions.assertEquals(nomeCategoriaAlterada, categoriaAlterada.getNome());
        Assertions.assertEquals(usuarioPadrao.getLogin(), categoriaAlterada.getUsuario().getLogin());
    }

    @Test
    void deveDeletarCategoria() {

        //Arrange
        String nomeCategoriaTransporte = "Transporte";
        Categoria categoriaTransporte = Categoria.builder().nome(nomeCategoriaTransporte).usuario(usuarioPadrao).build();

        categoriaRepository.save(categoriaTransporte);

        List<Categoria> categoriasAntesExclusao = categoriaRepository.findAll();

        //Assert
        //Verifica que a categoria foi incluida
        Assertions.assertEquals(1, categoriasAntesExclusao.size());

        String idCategoria = categoriasAntesExclusao.getFirst().getId();

        // Act
        categoriaService.deletarCategoria(idCategoria, usuarioPadrao.getLogin());

        //Assert
        List<Categoria> categoriasDepoisExclusao = categoriaRepository.findAll();
        Assertions.assertEquals(0, categoriasDepoisExclusao.size());
    }
}