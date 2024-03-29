package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.CategoriaRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.user.User;
import br.com.controle.financeiro.repositories.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    CategoriaRepository categoriaRepositoryMock;
    @Mock
    UserRepository userRepositoryMock;
    @Mock
    ValidacaoDadosUsuarioService validacaoDadosUsuarioServiceMock;

    @InjectMocks
    CategoriaService categoriaService;

    @Test
    void deveObterTodasCategorias() {

        //Arrange
        String loginUsuario = "user@login.com";

        List<Categoria> categoriasEsperadas = List.of(
                Categoria.builder().nome("Transporte").build(),
                Categoria.builder().nome("Alimentacao").build());

        Mockito.when(categoriaRepositoryMock.findAllCategoriasByUserLogin(loginUsuario)).thenReturn(categoriasEsperadas);

        //Act
        List<Categoria> categoriasObtidas = categoriaService.obterTodasCategorias(loginUsuario);

        //Assert
        Assertions.assertEquals(categoriasEsperadas, categoriasObtidas);
    }

    @Test
    void deveObterCategoriaPorId() {

        //Arrange
        String idCategoria = "123";
        String loginUsuario = "user@login.com";

        Categoria categoriaEsperada = Categoria.builder().nome("Alimentacao").build();
        Optional<Categoria> categoriaEsperadaOpt = Optional.of(categoriaEsperada);
        Mockito.when(categoriaRepositoryMock.findById(idCategoria)).thenReturn(categoriaEsperadaOpt);

        //Act
        Categoria categoriaResultado = categoriaService.obterCategoriaPorId(idCategoria, loginUsuario);

        //Assert
        Assertions.assertEquals(categoriaEsperada, categoriaResultado);

        Mockito.verify(validacaoDadosUsuarioServiceMock)
                .validarCategoriaDoUsuarioLogado(idCategoria, loginUsuario);
        Mockito.verify(categoriaRepositoryMock).findById(idCategoria);
    }

    @Test
    void deveCriarCategoria() {

        //Arrange
        String loginUsuario = "user@login.com";
        String nomeNovaCategoria = "Transporte";

        CategoriaRequestDTO novaCategoriaDto = new CategoriaRequestDTO(null, nomeNovaCategoria);

        List<Categoria> categoriasExistentes = List.of(Categoria.builder().nome("Alimentacao").build());
        Mockito.when(categoriaRepositoryMock.findAllCategoriasByUserLogin(loginUsuario)).thenReturn(categoriasExistentes);

        User usuario = User.builder().login(loginUsuario).id("1234").build();
        Mockito.when(userRepositoryMock.findByLogin(loginUsuario)).thenReturn(usuario);

        Categoria categoriaEsperada = Categoria.builder().nome(nomeNovaCategoria).user(usuario).build();
        Mockito.when(categoriaRepositoryMock.save(Mockito.any(Categoria.class))).thenReturn(categoriaEsperada);

        //Act
        Categoria categoriaResultado = categoriaService.criarCategoria(novaCategoriaDto, loginUsuario);

        //Assert
        Assertions.assertEquals(categoriaEsperada, categoriaResultado);
    }

    @Test
    void deveAtualizarCategoria() {

        //Arrange
        String loginUsuario = "user@login.com";
        String nomeNovaCategoria = "Lazer";

        CategoriaRequestDTO novaCategoriaDto = new CategoriaRequestDTO(null, nomeNovaCategoria);

        Categoria alimentacao = Categoria.builder().nome("Alimentacao").build();
        Categoria transporte = Categoria.builder().nome("Transporte").build();
        List<Categoria> categoriasExistentes = List.of(alimentacao, transporte);
        Mockito.when(categoriaRepositoryMock.findAllCategoriasByUserLogin(loginUsuario)).thenReturn(categoriasExistentes);

        User usuario = User.builder().login(loginUsuario).id("1234").build();
        String idCategoria = "id_Categoria";
        Mockito.when(categoriaRepositoryMock.findById(idCategoria)).thenReturn(Optional.of(transporte));

        Categoria categoriaEsperada = Categoria.builder().nome(nomeNovaCategoria).user(usuario).build();
        Mockito.when(categoriaRepositoryMock.save(Mockito.any(Categoria.class))).thenReturn(categoriaEsperada);

        //Act
        Categoria categoriaResultado = categoriaService.atualizarCategoria(idCategoria, novaCategoriaDto, loginUsuario);

        //Assert
        Assertions.assertEquals(categoriaEsperada, categoriaResultado);
    }

    @Test
    void naoDeveAtualizarCategoriaComMesmoNome() {

        //Arrange
        String loginUsuario = "user@login.com";
        String nomeNovaCategoria = "Transporte";

        CategoriaRequestDTO novaCategoriaDto = new CategoriaRequestDTO(null, nomeNovaCategoria);

        Categoria alimentacao = Categoria.builder().nome("Alimentacao").build();
        Categoria transporte = Categoria.builder().nome("Transporte").build();
        List<Categoria> categoriasExistentes = List.of(alimentacao, transporte);
        Mockito.when(categoriaRepositoryMock.findAllCategoriasByUserLogin(loginUsuario)).thenReturn(categoriasExistentes);

        String idCategoria = "id_Categoria";

        //Assert
        Assertions.assertThrows(
                //Assert
                NegocioException.class,
                //Act
                () -> categoriaService.atualizarCategoria(idCategoria, novaCategoriaDto, loginUsuario)
        );
    }

    @Test
    void deveDeletarCategoria() {

        //Arrange
        String loginUsuario = "user@login.com";
        String idCategoria = "id_Categoria";
        Mockito.doNothing().when(categoriaRepositoryMock).deleteById(idCategoria);

        //Act
        categoriaService.deletarCategoria(idCategoria, loginUsuario);

        //Assert
        Mockito.verify(validacaoDadosUsuarioServiceMock).validarCategoriaDoUsuarioLogado(idCategoria, loginUsuario);
        Mockito.verify(categoriaRepositoryMock).deleteById(idCategoria);
    }

    @Test
    void naoDeveLancarErroAoValidarCategoriaNomeDiferente() {

        //Arrange
        List<Categoria> categorias = List.of(Categoria.builder().nome("Alimentacao").build());

        //Act
        categoriaService.validarCategoriaComMesmoNome("Transporte", categorias);

        //Assert
        //Não lançar erro.
    }

    @Test
    void deveLancarErroAoValidarCategoriaMesmoNome() {

        //Arrange
        List<Categoria> categorias = List.of(
                Categoria.builder().nome("Transporte").build(),
                Categoria.builder().nome("Alimentacao").build());

        Assertions.assertThrows(
                //Assert
                NegocioException.class,
                //Act
                () -> categoriaService.validarCategoriaComMesmoNome("Alimentacao", categorias)
        );
    }

    @ParameterizedTest
    @MethodSource("provedorParametrosNomesCategorias")
    void naoDeveLancarErroAoValidarCategoriasComNomesDiferentes(String nomeCategoriaParametrizada, List<Categoria> categoriasParametrizada) {

        // Assume que a lista de categoria seja válida.
        // IGNORA o teste caso a lista de categorias seja null, mas não considera uma FALHA.
        Assumptions.assumeTrue(categoriasParametrizada != null);

        categoriaService.validarCategoriaComMesmoNome(nomeCategoriaParametrizada, categoriasParametrizada);
    }

    static Stream<Arguments> provedorParametrosNomesCategorias() {

        List<Categoria> categorias = List.of(
                Categoria.builder().nome("Alimentacao").build(),
                Categoria.builder().nome("Transporte").build(),
                Categoria.builder().nome("Educação").build());

        return Stream.of(
                Arguments.of("Lazer", categorias),
                Arguments.of("Saude", categorias),
                Arguments.of("Alimentacao", null),
                Arguments.of("Viagem", categorias)
        );
    }
}