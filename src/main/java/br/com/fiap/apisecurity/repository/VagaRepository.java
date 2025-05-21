package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VagaRepository extends JpaRepository<Vaga, UUID> {

    // Buscar todas as vagas de um pátio específico com determinado status
    List<Vaga> findByPatioAndStatus(Patio patio, StatusVaga status);

    // Buscar todas as vagas livres de um pátio
    List<Vaga> findByPatioIdAndStatus(UUID patioId, StatusVaga status);
}

