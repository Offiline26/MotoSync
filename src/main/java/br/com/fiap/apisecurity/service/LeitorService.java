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

    @Transactional
    @CachePut(value = "leitores", key = "#result.id")
    public LeitorDTO createLeitor(LeitorDTO dto) {
           if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (dto.getPatioId() == null || !userPatio.equals(dto.getPatioId())) {
                throw new SecurityException("Operador só pode criar leitor no próprio pátio.");
            }
        }

        patioService.readPatioById(dto.getPatioId());

        Patio patio = patioService.findById(dto.getPatioId())
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + dto.getPatioId()));

        Vaga vaga = vagaService.readVagaById(dto.getVagaId());

        Leitor leitor = new Leitor();
        leitor.setTipo(dto.getTipo());
        leitor.setPatio(patio);
        leitor.setVaga(vaga);

        return LeitorMapper.toDto(leitorRepository.save(leitor));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "leitores", key = "#id")
    public LeitorDTO readLeitorById(UUID id) {
        Leitor leitor = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado: " + id));

        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            UUID leitorPatio = leitor.getPatio() != null ? leitor.getPatio().getId() : null;
            if (leitorPatio == null || !userPatio.equals(leitorPatio)) {
                throw new SecurityException("Acesso negado: leitor de outro pátio.");
            }
        }

        return LeitorMapper.toDto(leitor);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "leitores", key = "'tipo-' + #tipo")
    public List<LeitorDTO> readByTipo(TipoLeitor tipo) {
        List<Leitor> leitores;
        if (authz.isAdmin()) {
            leitores = leitorRepository.findByTipo(tipo);
        } else {
            UUID patioId = authz.currentUserPatioIdOrThrow();
            leitores = leitorRepository.findByTipoAndPatio_Id(tipo, patioId);
        }
        return LeitorMapper.toDtoList(leitores);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "leitores", key = "'patio-' + #patio.id")
    public List<LeitorDTO> readByPatio(Patio patio) {
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (patio == null || patio.getId() == null || !userPatio.equals(patio.getId())) {
                throw new SecurityException("Operador só pode listar leitores do próprio pátio.");
            }
        }
        List<Leitor> leitores = leitorRepository.findByPatio(patio);
        return LeitorMapper.toDtoList(leitores);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "leitores", key = "'patio-' + #patioId")
    public List<LeitorDTO> readByPatio(UUID patioId) {
        patioService.readPatioById(patioId);
        List<Leitor> leitores;
        if (authz.isAdmin()) {
            leitores = leitorRepository.findByPatio_Id(patioId);
        } else {
            leitores = leitorRepository.findByPatio_Id(patioId);
        }
        return LeitorMapper.toDtoList(leitores);
    }

    @Transactional(readOnly = true)
    public Optional<LeitorDTO> readByVagaAndTipo(Vaga vaga, TipoLeitor tipo) {
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            UUID vagaPatio = vaga.getPatio() != null ? vaga.getPatio().getId() : null;
            if (vagaPatio == null || !userPatio.equals(vagaPatio)) {
                throw new SecurityException("Operador só pode consultar leitores de vagas do próprio pátio.");
            }
        }
        return leitorRepository.findByVagaAndTipo(vaga, tipo).map(LeitorMapper::toDto);
    }

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

    @Transactional
    @CachePut(value = "leitores", key = "#result.id")
    public LeitorDTO updateLeitor(UUID id, LeitorDTO dto) {
        Leitor leitor = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado: " + id));

        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            UUID leitorPatio = leitor.getPatio() != null ? leitor.getPatio().getId() : null;
            if (leitorPatio == null || !userPatio.equals(leitorPatio)) {
                throw new SecurityException("Operador não pode atualizar leitor de outro pátio.");
            }

            if (dto.getPatioId() != null && !userPatio.equals(dto.getPatioId())) {
                throw new SecurityException("Operador só pode vincular leitor ao próprio pátio.");
            }
        }

        patioService.readPatioById(dto.getPatioId());
        Patio patio = patioService.findById(dto.getPatioId())
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + dto.getPatioId()));

        Vaga vaga = vagaService.readVagaById(dto.getVagaId());

        leitor.setTipo(dto.getTipo());
        leitor.setPatio(patio);
        leitor.setVaga(vaga);

        return LeitorMapper.toDto(leitorRepository.save(leitor));
    }

    @Transactional
    @CacheEvict(value = "leitores", key = "#id")
    public void deleteLeitor(UUID id) {
        Leitor leitor = leitorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado: " + id));

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

