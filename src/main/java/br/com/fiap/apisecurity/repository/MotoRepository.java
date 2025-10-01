package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.enums.StatusMoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MotoRepository extends JpaRepository<Moto, UUID> {
    // Busca por placa (usado por readByPlaca / readByPlacaDto)
    Optional<Moto> findByPlaca(String placa);

    // Lista geral por status (usado por readAllMotosAtivas quando ADMIN)
    Page<Moto> findAllByStatus(StatusMoto status, Pageable pageable);

    // Filtro por vagas pertencentes ao pátio do operador (usado por readAllMotos)
    Page<Moto> findAllByVagaIdIn(Collection<UUID> vagaIds, Pageable pageable);

    // Filtro por status + vagas do pátio (usado por readAllMotosAtivas quando operador)
    Page<Moto> findAllByStatusAndVagaIdIn(StatusMoto status, Collection<UUID> vagaIds, Pageable pageable);
}
