package com.example.auth;

import com.example.auth.domain.Categoria;
import com.example.auth.domain.Conta;
import com.example.auth.domain.Lancamento;
import com.example.auth.domain.user.User;
import com.example.auth.domain.user.UserRole;
import com.example.auth.repositories.CategoriaRepository;
import com.example.auth.repositories.ContaRepository;
import com.example.auth.repositories.LancamentoRepository;
import com.example.auth.repositories.UserRepository;
import com.example.auth.services.ContaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class ControleFinanceiroApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControleFinanceiroApiApplication.class, args);
    }

//    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository,
                                               ContaService contaService,
                                               ContaRepository contaRepository,
                                               CategoriaRepository categoriaRepository,
                                               LancamentoRepository lancamentoRepository) {
        return args -> {
            var user = User.builder().login("rafael").password("123456").role(UserRole.ADMIN).build();

            var conta = Conta.builder().nome("Conta Conjunta").user(user).build();
            var contaCartao = Conta.builder().nome("Cartão Crédito").user(user).build();

            var categoriaAlimentacao = Categoria.builder().nome("Alimentação").user(user).build();
            var categoriaTranporte = Categoria.builder().nome("Transporte").user(user).build();

            var lancamento = Lancamento.builder()
                    .conta(conta).nome("Pipoca")
                    .categoria(categoriaAlimentacao)
                    .valor(BigDecimal.valueOf(123.45))
                    .data(LocalDate.now())
                    .build();
            var lancamento2 = Lancamento.builder()
                    .conta(conta).nome("Uber")
                    .categoria(categoriaTranporte)
                    .valor(BigDecimal.valueOf(321.54))
                    .data(LocalDate.now())
                    .build();

            userRepository.save(user);
//            contaService.createConta(conta);
//            contaService.createConta(contaCartao);
            categoriaRepository.save(categoriaAlimentacao);
            categoriaRepository.save(categoriaTranporte);
            lancamentoRepository.save(lancamento);
            lancamentoRepository.save(lancamento2);

//            List<Conta> allContas = contaService.getAllContas();
//
//            String idContaConjunta = null;
//            String idContaCartao = null;
//
//            System.out.println("Contas Antes");
//            for (Conta c : allContas) {
//                System.out.println("Conta = " + c.getNome());
//
//                if (c.getNome().equals("Conta Conjunta")) {
//                    idContaConjunta = c.getId();
//                }
//
//                if (c.getNome().equals("Cartão Crédito")) {
//                    idContaCartao = c.getId();
//                }
//            }
//
//            contaService.deleteConta(idContaCartao);
//            contaService.updateConta(idContaConjunta, Conta.builder().nome("Conta Conjunta 2").user(user).build());
//
//            System.out.println("Contas Depois");
//            allContas = contaService.getAllContas();
//            for (Conta c : allContas) {
//                System.out.println("Conta = " + c.getNome());
//            }
        };
    }

}
