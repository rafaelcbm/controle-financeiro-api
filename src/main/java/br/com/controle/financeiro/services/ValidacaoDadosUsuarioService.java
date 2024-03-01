package br.com.controle.financeiro.services;

import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.repositories.CategoriaRepository;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.LancamentoRepository;
import br.com.controle.financeiro.repositories.UserRepository;
import br.com.controle.financeiro.services.exception.NegocioException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidacaoDadosUsuarioService {

    private final ContaRepository contaRepository;

    private final CategoriaRepository categoriaRepository;

    private final LancamentoRepository lancamentoRepository;

    private final UserRepository userRepository;

    public ValidacaoDadosUsuarioService(ContaRepository contaRepository, CategoriaRepository categoriaRepository, LancamentoRepository lancamentoRepository, UserRepository userRepository) {
        this.contaRepository = contaRepository;
        this.categoriaRepository = categoriaRepository;
        this.lancamentoRepository = lancamentoRepository;
        this.userRepository = userRepository;
    }

    public void validarContaDoUsuarioLogado(String idConta, String userLogin) {

        UserDetails usuarioLogado = userRepository.findByLogin(userLogin);
        Optional<Conta> contaOptional = contaRepository.findById(idConta);

        if (contaOptional.isPresent()) {
            Conta conta = contaOptional.get();
            if (!conta.getUser().getUsername().equals(usuarioLogado.getUsername())) {
                throw new NegocioException("Conta não pertence ao usuário informado!");
            }
        } else {
            throw new NegocioException("Conta não encontrada!");
        }
    }

    public void validarCategoriaDoUsuarioLogado(String idCategoria, String userLogin) {

        UserDetails usuarioLogado = userRepository.findByLogin(userLogin);
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(idCategoria);

        if (categoriaOptional.isPresent()) {
            Categoria categoria = categoriaOptional.get();
            if (!categoria.getUser().getUsername().equals(usuarioLogado.getUsername())) {
                throw new NegocioException("Categoria não pertence ao usuário informado!");
            }
        } else {
            throw new NegocioException("Categoria não encontrada!");
        }
    }

    public void validarLancamentoDoUsuarioLogado(String idLancamento, String userLogin) {

        UserDetails usuarioLogado = userRepository.findByLogin(userLogin);
        Optional<Lancamento> lancamentoOptional = lancamentoRepository.findById(idLancamento);

        if (lancamentoOptional.isPresent()) {
            Lancamento lancamento = lancamentoOptional.get();
            if (!lancamento.getConta().getUser().getUsername().equals(usuarioLogado.getUsername())) {
                throw new NegocioException("Lançamento não pertence ao usuário informado!");
            }
        } else {
            throw new NegocioException("Lançamento não encontrado!");
        }
    }
}
