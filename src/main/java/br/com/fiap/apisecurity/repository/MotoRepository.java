package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Moto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MotoRepository extends JpaRepository<Moto, UUID> {
    // Exemplo de método customizado (opcional)
    Moto findByPlaca(String placa);
}
