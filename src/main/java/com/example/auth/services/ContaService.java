package com.example.auth.services;

import com.example.auth.controllers.dto.ContaRequestDTO;
import com.example.auth.domain.Conta;
import com.example.auth.domain.user.User;
import com.example.auth.repositories.ContaRepository;
import com.example.auth.repositories.UserRepository;
import com.example.auth.services.exception.NegocioException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    private final UserRepository userRepository;

    public ContaService(ContaRepository ContaRepository, UserRepository userRepository) {

        this.contaRepository = ContaRepository;
        this.userRepository = userRepository;
    }

    public List<Conta> obterTodasContas(String userLogin) {
        return contaRepository.findAllContasByUserLogin(userLogin);
    }

    public Conta obterContaPorId(String idConta, String userLogin) {
        return obterContaDoUsuario(idConta, userLogin);
    }

    @Transactional
    public Conta criarConta(ContaRequestDTO conta, String userLogin) {

        UserDetails usuario = userRepository.findByLogin(userLogin);

        Conta c = new Conta();
        c.setNome(conta.nome());
        c.setUser((User) usuario);

        return contaRepository.save(c);
    }

    @Transactional
    public Conta atualizarConta(String idConta, ContaRequestDTO contaRequestDTO, String userLogin) {

        Conta conta = obterContaDoUsuario(idConta, userLogin);
        conta.setNome(contaRequestDTO.nome());

        return contaRepository.save(conta);
    }

    @Transactional
    public void deletarConta(String idConta, String userLogin) {

        obterContaDoUsuario(idConta, userLogin);
        contaRepository.deleteById(idConta);
    }

    public Conta obterContaDoUsuario(String idConta, String userLogin) {

        UserDetails usuarioLogado = userRepository.findByLogin(userLogin);
        Optional<Conta> contaOptional = contaRepository.findById(idConta);

        if (contaOptional.isPresent()) {
            Conta conta = contaOptional.get();
            if (!conta.getUser().getUsername().equals(usuarioLogado.getUsername())) {
                throw new NegocioException("Conta não pertence ao usuário informado!");
            }
            return conta;
        } else {
            throw new NegocioException("Conta não encontrada!");
        }
    }
}
