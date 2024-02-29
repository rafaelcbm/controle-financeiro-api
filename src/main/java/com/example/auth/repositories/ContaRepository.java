package com.example.auth.repositories;

import com.example.auth.domain.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, String> {

    List<Conta> findAllContasByUserLogin(String userId);
}
