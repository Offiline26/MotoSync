package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.controller.usuario.Authz;
import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.mapper.VagaMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.repository.MotoRepository;
import br.com.fiap.apisecurity.repository.PatioRepository;
import br.com.fiap.apisecurity.repository.VagaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final MotoRepository motoRepository;
    private final PatioRepository patioRepository;
    private final Authz authz;

    @Autowired
    public VagaService(VagaRepository vagaRepository,
                       MotoRepository motoRepository,
                       PatioRepository patioRepository,
                       Authz authz) {
        this.vagaRepository = vagaRepository;
        this.motoRepository = motoRepository;
        this.patioRepository = patioRepository;
        this.authz = authz;
    }

    // ---------------- CREATE ----------------

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "vagas",    allEntries = true),
            @CacheEvict(cacheNames = "vagasAll", allEntries = true)
    })
    public VagaDTO createVaga(VagaDTO dto) {
        // ADMIN cria em qualquer pátio; OPERADOR só no próprio
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (dto.getPatioId() == null || !userPatio.equals(dto.getPatioId())) {
                throw new SecurityException("Operador só pode criar vaga no próprio pátio.");
            }
        }

        Vaga entity = VagaMapper.toEntity(dto);
        if (dto.getPatioId() != null) {
            Patio patio = patioRepository.findById(dto.getPatioId())
                    .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + dto.getPatioId()));
            entity.setPatio(patio);
        } else {
            entity.setPatio(null);
        }
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusVaga.LIVRE);

        Vaga saved = vagaRepository.save(entity);
        return VagaMapper.toDto(saved);
    }

    // ---------------- READ BY ID (Entity) ----------------

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "vagas", key="#id")
    public Vaga readVagaById(UUID id) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + id));

        if (authz.isAdmin()) return vaga;

        UUID userPatio = authz.currentUserPatioIdOrThrow();
        if (vaga.getPatio() == null || !userPatio.equals(vaga.getPatio().getId())) {
            throw new SecurityException("Acesso negado: vaga de outro pátio.");
        }
        return vaga;
    }

    // ---------------- READ ALL (Page) ----------------

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "vagas",
            key="'ADMIN:p:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#pageable.sort!=null ? #pageable.sort : 'UNSORTED')",
            condition="@authz.isAdmin()"
    )
    public Page<VagaDTO> readAllVagas(Pageable pageable) {
        if (authz.isAdmin()) {
            Page<Vaga> page = vagaRepository.findAll(pageable);
            return page.map(VagaMapper::toDto);
        }
        UUID patioId = authz.currentUserPatioIdOrThrow();
        Page<Vaga> page = vagaRepository.findAllByPatio_Id(patioId, pageable);
        return page.map(VagaMapper::toDto);
    }

    // ---------------- READ ALL (List) ----------------

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "vagasAll", condition="@authz.isAdmin()")
    public List<VagaDTO> readAllVagas() {
        if (authz.isAdmin()) {
            return vagaRepository.findAll().stream().map(VagaMapper::toDto).toList();
        }
        UUID patioId = authz.currentUserPatioIdOrThrow();
        return vagaRepository.findAllByPatio_Id(patioId).stream().map(VagaMapper::toDto).toList();
    }

    // ---------------- UPDATE ----------------

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "vagas",    allEntries = true),
            @CacheEvict(cacheNames = "vagasAll", allEntries = true)
    })
    public VagaDTO updateVaga(UUID id, VagaDTO dto) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + id));

        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            UUID alvoPatioId = (dto.getPatioId() != null) ? dto.getPatioId()
                    : (vaga.getPatio() != null ? vaga.getPatio().getId() : null);
            if (alvoPatioId == null || !userPatio.equals(alvoPatioId)) {
                throw new SecurityException("Operador só pode atualizar vaga do próprio pátio.");
            }
        }

        // aplica DTO -> Entity (mantendo seu mapper)
        VagaMapper.apply(dto, vaga, idPatio -> patioRepository.findById(idPatio)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + idPatio)));

        Vaga saved = vagaRepository.save(vaga);
        return VagaMapper.toDto(saved);
    }

    // ---------------- DELETE ----------------

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "vagas",    allEntries = true),
            @CacheEvict(cacheNames = "vagasAll", allEntries = true)
    })
    public void deleteVaga(UUID id) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada: " + id));
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (vaga.getPatio() == null || !userPatio.equals(vaga.getPatio().getId())) {
                throw new SecurityException("Operador só pode remover vaga do próprio pátio.");
            }
        }
        // se tiver moto, impedir ou liberar conforme sua regra atual
        if (vaga.getMoto() != null) {
            throw new IllegalStateException("Não é possível remover vaga ocupada.");
        }
        vagaRepository.delete(vaga);
    }

    // ---------------- FILTRO POR PÁTIO E STATUS ----------------

    @Transactional(readOnly = true)
    public List<VagaDTO> readByPatioAndStatus(UUID patioId, StatusVaga status) {
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (!userPatio.equals(patioId)) {
                throw new SecurityException("Operador só pode consultar o próprio pátio.");
            }
        }
        return vagaRepository.findAllByPatio_IdAndStatus(patioId, status)
                .stream().map(VagaMapper::toDto).toList();
    }
}