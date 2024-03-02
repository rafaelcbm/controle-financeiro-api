package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.LancamentoRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.repositories.LancamentoRepository;
import br.com.controle.financeiro.repositories.dto.LancamentoCompletoDTO;
import br.com.controle.financeiro.services.exception.NegocioException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LancamentoService {

    private final LancamentoRepository lancamentoRepository;

    private final ValidacaoDadosUsuarioService validacaoDadosUsuarioService;

    public LancamentoService(LancamentoRepository LancamentoRepository, ValidacaoDadosUsuarioService validacaoDadosUsuarioService) {

        this.lancamentoRepository = LancamentoRepository;
        this.validacaoDadosUsuarioService = validacaoDadosUsuarioService;
    }

    public List<Lancamento> obterTodosLancamentos(String userLogin) {
        return lancamentoRepository.findLancamentosByUser(userLogin);
    }

    public List<LancamentoCompletoDTO> obterTodosLancamentosCompletos(String userLogin) {
        return lancamentoRepository.findLancamentosCompletosByUser(userLogin);
    }

    public Lancamento obterLancamentoPorId(String idLancamento, String userLogin) {

        validacaoDadosUsuarioService.validarLancamentoDoUsuarioLogado(idLancamento, userLogin);

        return lancamentoRepository.findById(idLancamento).orElseThrow();
    }

    public List<Lancamento> obterLancamentosPorCompetencia(Integer competencia, String userLogin) {

        List<Lancamento> todosLancamentos = obterTodosLancamentos(userLogin);

        int ano = competencia / 100;
        int mes = competencia % 100;

        List<Lancamento> lancamentosCompetencia = todosLancamentos.stream()
                .filter(lancamento -> lancamento.getData().getYear() == ano &&
                        lancamento.getData().getMonthValue() == mes)
                .toList();

        return lancamentosCompetencia;
    }

    @Transactional
    public Lancamento criarLancamento(LancamentoRequestDTO lancamentoDTO, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(lancamentoDTO.idConta(), userLogin);
        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(lancamentoDTO.idCategoria(), userLogin);

        if (lancamentoDTO.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException("Valor do lançamento informado deve ser maior que 0!");
        }

        Lancamento lancamento = Lancamento.builder()
                .nome(lancamentoDTO.nome())
                .conta(Conta.builder().id(lancamentoDTO.idConta()).build())
                .categoria(Categoria.builder().id(lancamentoDTO.idCategoria()).build())
                .data(lancamentoDTO.parseDate())
                .valor(lancamentoDTO.valor())
                .pago(lancamentoDTO.pago())
                .build();

        return lancamentoRepository.save(lancamento);
    }

    @Transactional
    public Lancamento atualizarLancamento(String idLancamento, LancamentoRequestDTO lancamentoDTO, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(lancamentoDTO.idConta(), userLogin);
        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(lancamentoDTO.idCategoria(), userLogin);
        validacaoDadosUsuarioService.validarLancamentoDoUsuarioLogado(idLancamento, userLogin);

        if (lancamentoDTO.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException("Valor do lançamento informado deve ser maior que 0!");
        }

        Lancamento lancamento = lancamentoRepository.findById(idLancamento).orElseThrow();

        lancamento.setNome(lancamentoDTO.nome());
        lancamento.setConta(Conta.builder().id(lancamentoDTO.idConta()).build());
        lancamento.setCategoria(Categoria.builder().id(lancamentoDTO.idCategoria()).build());
        lancamento.setData(lancamentoDTO.parseDate());
        lancamento.setValor(lancamentoDTO.valor());
        lancamento.setPago(lancamentoDTO.pago());

        return lancamentoRepository.save(lancamento);
    }

    @Transactional
    public Lancamento atualizarLancamentoComoPago(String idLancamento, String userLogin) {

        validacaoDadosUsuarioService.validarLancamentoDoUsuarioLogado(idLancamento, userLogin);
        Lancamento lancamento = lancamentoRepository.findById(idLancamento).orElseThrow();

        if (lancamento.isPago()) {
            throw new NegocioException("Lançamento já está pago!");
        } else {
            lancamento.setPago(true);
            return lancamentoRepository.save(lancamento);
        }
    }

    @Transactional
    public Lancamento atualizarLancamentoComoNaoPago(String idLancamento, String userLogin) {

        validacaoDadosUsuarioService.validarLancamentoDoUsuarioLogado(idLancamento, userLogin);
        Lancamento lancamento = lancamentoRepository.findById(idLancamento).orElseThrow();

        if (!lancamento.isPago()) {
            throw new NegocioException("Lançamento já não está pago!");
        } else {
            lancamento.setPago(false);
            return lancamentoRepository.save(lancamento);
        }
    }

    @Transactional
    public void deletarLancamento(String idLancamento, String userLogin) {

        validacaoDadosUsuarioService.validarLancamentoDoUsuarioLogado(idLancamento, userLogin);
        lancamentoRepository.deleteById(idLancamento);
    }
}
