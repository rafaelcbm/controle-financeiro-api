package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.CategoriaRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.user.User;
import br.com.controle.financeiro.repositories.CategoriaRepository;
import br.com.controle.financeiro.repositories.UserRepository;
import br.com.controle.financeiro.services.exception.NegocioException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final UserRepository userRepository;

    private final ValidacaoDadosUsuarioService validacaoDadosUsuarioService;

    public CategoriaService(CategoriaRepository CategoriaRepository, UserRepository userRepository, ValidacaoDadosUsuarioService validacaoDadosUsuarioService) {

        this.categoriaRepository = CategoriaRepository;
        this.userRepository = userRepository;
        this.validacaoDadosUsuarioService = validacaoDadosUsuarioService;
    }

    public List<Categoria> obterTodasCategorias(String userLogin) {
        return categoriaRepository.findAllCategoriasByUserLogin(userLogin);
    }

    public Categoria obterCategoriaPorId(String idCategoria, String userLogin) {

        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(idCategoria, userLogin);

        return categoriaRepository.findById(idCategoria).orElseThrow();
    }

    @Transactional
    public Categoria criarCategoria(CategoriaRequestDTO categoriaDTO, String userLogin) {

        List<Categoria> categorias = this.obterTodasCategorias(userLogin);
        validarCategoriaComMesmoNome(categoriaDTO.nome(), categorias);

        UserDetails usuario = userRepository.findByLogin(userLogin);
        Categoria c = new Categoria();
        c.setNome(categoriaDTO.nome());
        c.setUser((User) usuario);

        return categoriaRepository.save(c);
    }

    @Transactional
    public Categoria atualizarCategoria(String idCategoria, CategoriaRequestDTO categoriaDTO, String userLogin) {

        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(idCategoria, userLogin);

        List<Categoria> categorias = this.obterTodasCategorias(userLogin);
        validarCategoriaComMesmoNome(categoriaDTO.nome(), categorias);

        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow();
        categoria.setNome(categoriaDTO.nome());

        return categoriaRepository.save(categoria);
    }

    protected void validarCategoriaComMesmoNome(String nomeCategoria, List<Categoria> categorias) {

        boolean possuiCategoriaComMesmoNome = categorias.stream()
                .anyMatch(c -> c.getNome().equals(nomeCategoria));
        if (possuiCategoriaComMesmoNome) {
            throw new NegocioException("Categoria com nome informado já existente.");
        }
    }

    @Transactional
    public void deletarCategoria(String idCategoria, String userLogin) {

        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(idCategoria, userLogin);
        categoriaRepository.deleteById(idCategoria);
    }
}
