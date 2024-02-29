package com.example.auth.services;

import com.example.auth.controllers.dto.LancamentoRequestDTO;
import com.example.auth.domain.Categoria;
import com.example.auth.domain.Conta;
import com.example.auth.domain.Lancamento;
import com.example.auth.repositories.LancamentoRepository;
import com.example.auth.repositories.UserRepository;
import com.example.auth.repositories.dto.LancamentoCompletoDTO;
import com.example.auth.services.exception.NegocioException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;

    private final UserRepository userRepository;

    public LancamentoService(LancamentoRepository LancamentoRepository, UserRepository userRepository) {

        this.lancamentoRepository = LancamentoRepository;
        this.userRepository = userRepository;
    }

    public List<Lancamento> obterTodasLancamentos(String userLogin) {
        return lancamentoRepository.findLancamentosByUser(userLogin);
    }

    public List<LancamentoCompletoDTO> obterTodosLancamentosCompletos(String userLogin) {
        return lancamentoRepository.findLancamentosCompletosByUser(userLogin);
    }

    public Lancamento obterLancamentoPorId(String idLancamento, String userLogin) {
        return obterLancamentoDoUsuario(idLancamento, userLogin);
    }

    @Transactional
    public Lancamento criarLancamento(LancamentoRequestDTO lancamentoDTO, String userLogin) {

        UserDetails usuario = userRepository.findByLogin(userLogin);
        //TODO: verificar se o idConta e idCategoria passados pertencem ao usuario

        Lancamento lancamento = Lancamento.builder()
                .nome(lancamentoDTO.nome())
                .conta(Conta.builder().id(lancamentoDTO.idConta()).build())
                .categoria(Categoria.builder().id(lancamentoDTO.idCategoria()).build())
                .data(lancamentoDTO.parseDate())
                .valor(lancamentoDTO.valor())
                .build();

        return lancamentoRepository.save(lancamento);
    }

    @Transactional
    public Lancamento atualizarLancamento(String idLancamento, LancamentoRequestDTO lancamentoDTO, String userLogin) {

        Lancamento lancamento = obterLancamentoDoUsuario(idLancamento, userLogin);
        lancamento.setNome(lancamentoDTO.nome());
        //TODO: verificar se o idConta e idCategoria passados pertencem ao usuario
        lancamento.setConta(Conta.builder().id(lancamentoDTO.idConta()).build());
        lancamento.setCategoria(Categoria.builder().id(lancamentoDTO.idCategoria()).build());
        lancamento.setData(lancamentoDTO.parseDate());
        lancamento.setValor(lancamentoDTO.valor());
        lancamento.setPago(lancamentoDTO.pago());

        return lancamentoRepository.save(lancamento);
    }

    @Transactional
    public Lancamento atualizarLancamentoComoPago(String idLancamento, String userLogin) {

        Lancamento lancamento = obterLancamentoDoUsuario(idLancamento, userLogin);

        if (lancamento.isPago()) {
            throw new NegocioException("Lançamento já está pago!");
        } else {
            lancamento.setPago(true);
            return lancamentoRepository.save(lancamento);
        }
    }

    @Transactional
    public Lancamento atualizarLancamentoComoNaoPago(String idLancamento, String userLogin) {

        Lancamento lancamento = obterLancamentoDoUsuario(idLancamento, userLogin);

        if (!lancamento.isPago()) {
            throw new NegocioException("Lançamento já não está pago!");
        } else {
            lancamento.setPago(false);
            return lancamentoRepository.save(lancamento);
        }
    }

    @Transactional
    public void deletarLancamento(String idLancamento, String userLogin) {

        obterLancamentoDoUsuario(idLancamento, userLogin);
        lancamentoRepository.deleteById(idLancamento);
    }

    public Lancamento obterLancamentoDoUsuario(String idLancamento, String userLogin) {

        UserDetails usuarioLogado = userRepository.findByLogin(userLogin);
        Optional<Lancamento> lancamentoOptional = lancamentoRepository.findById(idLancamento);

        if (lancamentoOptional.isPresent()) {
            Lancamento lancamento = lancamentoOptional.get();
            if (!lancamento.getConta().getUser().getUsername().equals(usuarioLogado.getUsername())) {
                throw new NegocioException("Lançamento não pertence ao usuário informado!");
            }
            return lancamento;
        } else {
            throw new NegocioException("Lançamento não encontrado!");
        }
    }
}
