package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.ContaRequestDTO;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.user.User;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.UserRepository;
import br.com.controle.financeiro.services.exception.NegocioException;
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
        return contaRepository.findById(idConta).orElseThrow();
    }

    @Transactional
    public Conta criarConta(ContaRequestDTO contaDTO, String userLogin) {

        List<Conta> contas = this.obterTodasContas(userLogin);
        validarContaComMesmoNome(contaDTO.nome(), contas);

        UserDetails usuario = userRepository.findByLogin(userLogin);
        Conta c = new Conta();
        c.setNome(contaDTO.nome());
        c.setUser((User) usuario);

        return contaRepository.save(c);
    }

    @Transactional
    public Conta atualizarConta(String idConta, ContaRequestDTO contaDTO, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(idConta, userLogin);

        List<Conta> contas = this.obterTodasContas(userLogin);
        validarContaComMesmoNome(contaDTO.nome(), contas);

        Conta conta = contaRepository.findById(idConta).orElseThrow();
        conta.setNome(contaDTO.nome());

        return contaRepository.save(conta);
    }

    protected void validarContaComMesmoNome(String nomeConta, List<Conta> contas) {

        boolean possuiContaComMesmoNome = contas.stream()
                .anyMatch(c -> c.getNome().equals(nomeConta));
        if (possuiContaComMesmoNome) {
            throw new NegocioException("Conta com nome informado já existente.");
        }
    }

    @Transactional
    public void deletarConta(String idConta, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(idConta, userLogin);
        contaRepository.deleteById(idConta);
    }
}
