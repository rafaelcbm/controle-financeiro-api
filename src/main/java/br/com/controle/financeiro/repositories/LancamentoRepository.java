package br.com.controle.financeiro.repositories;

import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.repositories.dto.LancamentoCompletoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, String> {

    @Query("SELECT l FROM Lancamento l WHERE l.conta.user.login = :userLogin")
    List<Lancamento> findLancamentosByUser(@Param("userLogin") String userLogin);

    @Query("SELECT new br.com.controle.financeiro.repositories.dto.LancamentoCompletoDTO(l.id, l.nome, c.nome, cat.nome, l.data, l.valor, l.pago) FROM Lancamento l JOIN l.conta c JOIN l.categoria cat WHERE l.conta.user.login = :userLogin")
    List<LancamentoCompletoDTO> findLancamentosCompletosByUser(@Param("userLogin") String userLogin);

}
