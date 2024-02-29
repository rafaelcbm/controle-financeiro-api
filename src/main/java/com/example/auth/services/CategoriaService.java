package com.example.auth.services;

import com.example.auth.controllers.dto.CategoriaRequestDTO;
import com.example.auth.domain.Categoria;
import com.example.auth.domain.user.User;
import com.example.auth.repositories.CategoriaRepository;
import com.example.auth.repositories.UserRepository;
import com.example.auth.services.exception.NegocioException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final UserRepository userRepository;

    public CategoriaService(CategoriaRepository CategoriaRepository, UserRepository userRepository) {

        this.categoriaRepository = CategoriaRepository;
        this.userRepository = userRepository;
    }

    public List<Categoria> obterTodasCategorias(String userLogin) {
        return categoriaRepository.findAllCategoriasByUserLogin(userLogin);
    }

    public Categoria obterCategoriaPorId(String idCategoria, String userLogin) {
        return obterCategoriaDoUsuario(idCategoria, userLogin);
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

        Categoria categoria = obterCategoriaDoUsuario(idCategoria, userLogin);
        categoria.setNome(categoriaRequestDTO.nome());

        return categoriaRepository.save(categoria);
    }

    @Transactional
    public void deletarCategoria(String idCategoria, String userLogin) {

        obterCategoriaDoUsuario(idCategoria, userLogin);
        categoriaRepository.deleteById(idCategoria);
    }

    public Categoria obterCategoriaDoUsuario(String idCategoria, String userLogin) {

        UserDetails usuarioLogado = userRepository.findByLogin(userLogin);
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(idCategoria);

        if (categoriaOptional.isPresent()) {
            Categoria categoria = categoriaOptional.get();
            if (!categoria.getUser().getUsername().equals(usuarioLogado.getUsername())) {
                throw new NegocioException("Categoria não pertence ao usuário informado!");
            }
            return categoria;
        } else {
            throw new NegocioException("Categoria não encontrada!");
        }
    }
}
