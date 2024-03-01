package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.CategoriaRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.user.User;
import br.com.controle.financeiro.repositories.CategoriaRepository;
import br.com.controle.financeiro.repositories.UserRepository;
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
    public Categoria criarCategoria(CategoriaRequestDTO categoria, String userLogin) {

        UserDetails usuario = userRepository.findByLogin(userLogin);

        Categoria c = new Categoria();
        c.setNome(categoria.nome());
        c.setUser((User) usuario);

        return categoriaRepository.save(c);
    }

    @Transactional
    public Categoria atualizarCategoria(String idCategoria, CategoriaRequestDTO categoriaRequestDTO, String userLogin) {

        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(idCategoria, userLogin);
        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow();
        categoria.setNome(categoriaRequestDTO.nome());

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void deletarCategoria(String idCategoria, String userLogin) {

        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(idCategoria, userLogin);
        categoriaRepository.deleteById(idCategoria);
    }

}
