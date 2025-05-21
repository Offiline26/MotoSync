package br.com.fiap.apisecurity.repository;

import br.com.fiap.apisecurity.model.Leitor;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.model.enums.TipoLeitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeitorRepository extends JpaRepository<Leitor, UUID> {

    // Buscar leitores por tipo (ENTRADA ou VAGA)
    List<Leitor> findByTipo(TipoLeitor tipo);

    // Buscar leitor de vaga específico
    Optional<Leitor> findByVagaAndTipo(Vaga vaga, TipoLeitor tipo);

    // Buscar leitores de um pátio
    List<Leitor> findByPatio(Patio patio);
}

