package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Registro;
import br.com.fiap.apisecurity.model.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RegistroRepository extends JpaRepository<Registro, UUID> {

    // Buscar todos os registros de uma moto
    List<Registro> findByMoto(Moto moto);

    // Buscar registros de uma moto por tipo de movimentação (ex: apenas ENTRADA)
    List<Registro> findByMotoAndTipo(Moto moto, TipoMovimentacao tipo);

    // Buscar registros de um período
    List<Registro> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByMotoId(UUID motoId);
}

