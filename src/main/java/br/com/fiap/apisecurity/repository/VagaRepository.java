package br.com.fiap.apisecurity.repository;

import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;
import java.util.stream.Collectors;

public interface VagaRepository extends JpaRepository<Vaga, UUID> {

    Page<Vaga> findAllByPatio_Id(UUID patioId, Pageable pageable);
    List<Vaga> findAllByPatio_Id(UUID patioId);
    List<Vaga> findAllByPatio_IdAndStatus(UUID patioId, StatusVaga status);
    long countByPatio_Id(UUID patioId);

    @Query("select v.id from Vaga v where v.patio.id = :patioId")
    List<UUID> findAllIdsByPatioId(@Param("patioId") UUID patioId);

    @Query("select v.id, v.identificacao from Vaga v where v.id in :ids")
    List<Object[]> findIdAndCodigoByIdIn(@Param("ids") Set<UUID> ids);

    default Map<UUID, String> findIdentificacoesByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        List<Object[]> rows = findIdAndCodigoByIdIn(ids);
        return rows.stream().collect(Collectors.toMap(
                r -> (UUID) r[0],
                r -> (String) r[1]
        ));
}
}

