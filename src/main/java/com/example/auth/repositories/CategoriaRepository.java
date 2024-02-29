package com.example.auth.repositories;

import com.example.auth.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, String> {

    List<Categoria> findAllCategoriasByUserLogin(String userId);
}
