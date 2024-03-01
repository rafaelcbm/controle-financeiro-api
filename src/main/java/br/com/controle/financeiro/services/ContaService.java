package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.ContaRequestDTO;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.user.User;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    private final UserRepository userRepository;

    private final ValidacaoDadosUsuarioService validacaoDadosUsuarioService;

    public ContaService(ContaRepository ContaRepository, UserRepository userRepository, ValidacaoDadosUsuarioService validacaoDadosUsuarioService) {

        this.contaRepository = ContaRepository;
        this.userRepository = userRepository;
        this.validacaoDadosUsuarioService = validacaoDadosUsuarioService;
    }

    public List<Conta> obterTodasContas(String userLogin) {
        return contaRepository.findAllContasByUserLogin(userLogin);
    }

    public Conta obterContaPorId(String idConta, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(idConta, userLogin);
        return  contaRepository.findById(idConta).orElseThrow();
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

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(idConta, userLogin);
        Conta conta = contaRepository.findById(idConta).orElseThrow();
        conta.setNome(contaRequestDTO.nome());

        return contaRepository.save(conta);
    }

    @Transactional
    public void deletarConta(String idConta, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(idConta, userLogin);
        contaRepository.deleteById(idConta);
    }
}
