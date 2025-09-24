package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.enums.StatusMoto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MotoRepository extends JpaRepository<Moto, UUID> {
    // Case-insensitive (evita duplicidade só por maiúscula/minúscula)
    Moto findByPlacaIgnoreCase(String placa);

    // Checagens de unicidade (útil em create/update)
    boolean existsByPlacaIgnoreCase(String placa);
    boolean existsByPlacaIgnoreCaseAndIdNot(String placa, UUID id);

    // Listagens que ignoram INATIVADA (combina com seu "soft delete")
    Page<Moto> findByStatusNot(StatusMoto status, Pageable pageable);
    Page<Moto> findByStatus(StatusMoto status, Pageable pageable);
}
