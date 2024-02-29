package com.example.auth.domain;

import com.example.auth.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nome;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "categoria")
    private List<Lancamento> lancamentos;

}
