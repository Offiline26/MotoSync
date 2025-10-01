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

    List<Leitor> findByTipo(TipoLeitor tipo);

    Optional<Leitor> findByVagaAndTipo(Vaga vaga, TipoLeitor tipo);

    List<Leitor> findByPatio(Patio patio);

    List<Leitor> findByPatio_Id(UUID patioId);

    Page<Leitor> findAllByPatio_Id(UUID patioId, Pageable pageable);

    List<Leitor> findByTipoAndPatio_Id(TipoLeitor tipo, UUID patioId);
}

