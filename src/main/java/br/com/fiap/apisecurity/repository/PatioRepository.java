package br.com.fiap.apisecurity.repository;

import br.com.fiap.apisecurity.model.Patio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatioRepository extends JpaRepository<Patio, UUID> {

    List<Patio> findByCidadeContainingIgnoreCase(String cidade);

    Optional<Patio> findByNomeIgnoreCase(String nome);

    Page<Patio> findAll(Pageable pageable);
}

