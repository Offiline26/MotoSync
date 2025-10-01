package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.controller.usuario.Authz;
import br.com.fiap.apisecurity.dto.LeitorDTO;
import br.com.fiap.apisecurity.mapper.LeitorMapper;
import br.com.fiap.apisecurity.model.Leitor;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.enums.TipoLeitor;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.repository.LeitorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeitorService {

    private final LeitorRepository leitorRepository;
    private final PatioService patioService;
    private final VagaService vagaService;
    private final Authz authz;

    @Autowired
    public LeitorService(LeitorRepository leitorRepository,
                         PatioService patioService,
                         VagaService vagaService,
                         Authz authz) {
        this.leitorRepository = leitorRepository;
        this.patioService = patioService;
        this.vagaService = vagaService;
        this.authz = authz;
    }

    // ---------------- CREATE ----------------
    @Transactional
    @CachePut(value = "leitores", key = "#result.id")
    public LeitorDTO createLeitor(LeitorDTO dto) {
        // Regra de segurança: ADMIN pode tudo; OPERADOR só no próprio pátio.
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (dto.getPatioId() == null || !userPatio.equals(dto.getPatioId())) {
                throw new SecurityException("Operador só pode criar leitor no próprio pátio.");
            }
        }

        // Garantir que o pátio existe e aplicar regra de acesso (via readPatioById lança se não puder)
        patioService.readPatioById(dto.getPatioId());

        // Buscar ENTIDADES (coerente com seu código atual)
        Patio patio = patioService.findById(dto.getPatioId())
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + dto.getPatioId()));

        // VagaService.readVagaById já aplica segurança por pátio para OPERADOR
        Vaga vaga = vagaService.readVagaById(dto.getVagaId());

        Leitor leitor = new Leitor();
        leitor.setTipo(dto.getTipo());
        leitor.setPatio(patio);
        leitor.setVaga(vaga);

        return LeitorMapper.toDto(leitorRepository.save(leitor));
    }

    // ---------------- READ BY ID ----------------
    @Transactional(readOnly = true)
    @Cacheable(value = "leitores", key = "#id")
    public LeitorDTO readLeitorById(UUID id) {
        Leitor leitor = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado: " + id));

        // Segurança: OPERADOR só pode ver leitor do seu pátio
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            UUID leitorPatio = leitor.getPatio() != null ? leitor.getPatio().getId() : null;
            if (leitorPatio == null || !userPatio.equals(leitorPatio)) {
                throw new SecurityException("Acesso negado: leitor de outro pátio.");
            }
        }

        return LeitorMapper.toDto(leitor);
    }

    // ---------------- READ BY TIPO ----------------
    @Transactional(readOnly = true)
    @Cacheable(value = "leitores", key = "'tipo-' + #tipo")
    public List<LeitorDTO> readByTipo(TipoLeitor tipo) {
        List<Leitor> leitores;
        if (authz.isAdmin()) {
            leitores = leitorRepository.findByTipo(tipo);
        } else {
            UUID patioId = authz.currentUserPatioIdOrThrow();
            // precise de um método filtrando por pátio; se tiver apenas findByTipo, filtramos em memória
            leitores = leitorRepository.findByTipoAndPatio_Id(tipo, patioId);
        }
        return LeitorMapper.toDtoList(leitores);
    }

    // ---------------- READ BY PÁTIO (ENTIDADE) ----------------
    @Transactional(readOnly = true)
    @Cacheable(value = "leitores", key = "'patio-' + #patio.id")
    public List<LeitorDTO> readByPatio(Patio patio) {
        // Segurança: OPERADOR só pode no seu pátio
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (patio == null || patio.getId() == null || !userPatio.equals(patio.getId())) {
                throw new SecurityException("Operador só pode listar leitores do próprio pátio.");
            }
        }
        List<Leitor> leitores = leitorRepository.findByPatio(patio);
        return LeitorMapper.toDtoList(leitores);
    }

    // ---------------- READ BY PÁTIO (UUID) - Overload útil para Controller ----------------
    @Transactional(readOnly = true)
    @Cacheable(value = "leitores", key = "'patio-' + #patioId")
    public List<LeitorDTO> readByPatio(UUID patioId) {
        // Garante existência + regra de acesso usando o PatioService (lança se não puder)
        patioService.readPatioById(patioId);
        List<Leitor> leitores;
        if (authz.isAdmin()) {
            leitores = leitorRepository.findByPatio_Id(patioId);
        } else {
            // Para operador, o check acima já garante que patioId == userPatio
            leitores = leitorRepository.findByPatio_Id(patioId);
        }
        return LeitorMapper.toDtoList(leitores);
    }

    // ---------------- READ BY VAGA AND TIPO ----------------
    @Transactional(readOnly = true)
    public Optional<LeitorDTO> readByVagaAndTipo(Vaga vaga, TipoLeitor tipo) {
        // Segurança: para operador, a vaga precisa ser do seu pátio
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            UUID vagaPatio = vaga.getPatio() != null ? vaga.getPatio().getId() : null;
            if (vagaPatio == null || !userPatio.equals(vagaPatio)) {
                throw new SecurityException("Operador só pode consultar leitores de vagas do próprio pátio.");
            }
        }
        return leitorRepository.findByVagaAndTipo(vaga, tipo).map(LeitorMapper::toDto);
    }

    // ---------------- READ ALL ----------------
    @Transactional(readOnly = true)
    @Cacheable(
            value="leitores",
            key="'ADMIN:all:p:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#pageable.sort!=null ? #pageable.sort : 'UNSORTED')",
            condition="@authz.isAdmin()"
    )
    public Page<LeitorDTO> readAllLeitores(Pageable pageable) {
        Page<Leitor> page;
        if (authz.isAdmin()) {
            page = leitorRepository.findAll(pageable);
        } else {
            UUID patioId = authz.currentUserPatioIdOrThrow();
            page = leitorRepository.findAllByPatio_Id(patioId, pageable);
        }
        List<LeitorDTO> dtoList = page.getContent().stream()
                .map(LeitorMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // ---------------- UPDATE ----------------
    @Transactional
    @CachePut(value = "leitores", key = "#result.id")
    public LeitorDTO updateLeitor(UUID id, LeitorDTO dto) {
        Leitor leitor = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado: " + id));

        // Segurança: OPERADOR só pode alterar leitor do seu pátio
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            UUID leitorPatio = leitor.getPatio() != null ? leitor.getPatio().getId() : null;
            if (leitorPatio == null || !userPatio.equals(leitorPatio)) {
                throw new SecurityException("Operador não pode atualizar leitor de outro pátio.");
            }
            // Se trocar pátio/vaga via DTO, também precisa ser o mesmo pátio
            if (dto.getPatioId() != null && !userPatio.equals(dto.getPatioId())) {
                throw new SecurityException("Operador só pode vincular leitor ao próprio pátio.");
            }
        }

        // Validar existência e acesso ao pátio informado
        patioService.readPatioById(dto.getPatioId());
        Patio patio = patioService.findById(dto.getPatioId())
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + dto.getPatioId()));

        // Vaga (o VagaService já valida pátio para operador)
        Vaga vaga = vagaService.readVagaById(dto.getVagaId());

        leitor.setTipo(dto.getTipo());
        leitor.setPatio(patio);
        leitor.setVaga(vaga);

        return LeitorMapper.toDto(leitorRepository.save(leitor));
    }

    // ---------------- DELETE ----------------
    @Transactional
    @CacheEvict(value = "leitores", key = "#id")
    public void deleteLeitor(UUID id) {
        Leitor leitor = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado: " + id));

        // Segurança: OPERADOR só pode excluir do próprio pátio
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            UUID leitorPatio = leitor.getPatio() != null ? leitor.getPatio().getId() : null;
            if (leitorPatio == null || !userPatio.equals(leitorPatio)) {
                throw new SecurityException("Operador não pode excluir leitor de outro pátio.");
            }
        }
        leitorRepository.delete(leitor);
    }
}

