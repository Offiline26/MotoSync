package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Patio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatioRepository extends JpaRepository<Patio, UUID> {

    // Buscar pátios por cidade (caso queira agrupar ou filtrar visualmente)
    List<Patio> findByCidade(String cidade);

    // Buscar por nome exato
    Patio findByNome(String nome);
}

