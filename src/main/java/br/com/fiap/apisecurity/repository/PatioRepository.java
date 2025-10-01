package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Patio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatioRepository extends JpaRepository<Patio, UUID> {

    // Suporte a readByCidade (ADMIN lista; operador só o seu pátio)
    List<Patio> findByCidadeContainingIgnoreCase(String cidade);

    // Suporte a readByNome (ADMIN por nome; operador valida contra o seu)
    Optional<Patio> findByNomeIgnoreCase(String nome);

    // Leitura paginada geral (ADMIN) — já vem de JpaRepository#findAll(Pageable)
    Page<Patio> findAll(Pageable pageable);
}

