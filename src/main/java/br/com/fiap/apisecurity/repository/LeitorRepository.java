package br.com.fiap.apisecurity.repository;

import br.com.fiap.apisecurity.model.Leitor;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.model.enums.TipoLeitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeitorRepository extends JpaRepository<Leitor, UUID> {

    // Buscar leitores por tipo (ENTRADA, SAIDA, etc.)
    List<Leitor> findByTipo(TipoLeitor tipo);

    // Buscar leitor de uma vaga específico por tipo
    Optional<Leitor> findByVagaAndTipo(Vaga vaga, TipoLeitor tipo);

    // Buscar leitores de um pátio (por entidade)
    List<Leitor> findByPatio(Patio patio);

    // ----- novos (coerentes com o Service) -----
    // Buscar leitores de um pátio (por ID do pátio)
    List<Leitor> findByPatio_Id(UUID patioId);

    // Paginado por pátio (usado em readAllLeitores para OPERADOR)
    Page<Leitor> findAllByPatio_Id(UUID patioId, Pageable pageable);

    // Buscar por tipo filtrando por pátio (usado em readByTipo para OPERADOR)
    List<Leitor> findByTipoAndPatio_Id(TipoLeitor tipo, UUID patioId);
}

