package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {

    // Buscar um sensor pelo código único (caso de leitura por hardware)
    Optional<Sensor> findByCodigoUnico(String codigoUnico);

}

