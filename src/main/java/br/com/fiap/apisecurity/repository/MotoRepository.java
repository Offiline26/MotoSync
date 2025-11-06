package br.com.fiap.apisecurity.repository;

import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.enums.StatusMoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface MotoRepository extends JpaRepository<Moto, UUID> {

    Optional<Moto> findByPlaca(String placa);

    Page<Moto> findAllByStatus(StatusMoto status, Pageable pageable);

    Page<Moto> findAllByVagaIdIn(Collection<UUID> vagaIds, Pageable pageable);

    Page<Moto> findAllByStatusAndVagaIdIn(StatusMoto status, Collection<UUID> vagaIds, Pageable pageable);
}
