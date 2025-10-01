package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.mapper.VagaMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.repository.MotoRepository;
import br.com.fiap.apisecurity.repository.PatioRepository;
import br.com.fiap.apisecurity.repository.VagaRepository;
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

    @Autowired
    public VagaService(VagaRepository vagaRepository, MotoRepository motoRepository, PatioRepository patioRepository) {
        this.vagaRepository = vagaRepository;
        this.motoRepository = motoRepository;
        this.patioRepository = patioRepository;
    }

    // Create
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "vagas",    allEntries = true),
            @CacheEvict(cacheNames = "vagasAll", allEntries = true)
    })
    public VagaDTO createVaga(VagaDTO vagaDTO) {
        Vaga vaga = VagaMapper.toEntity(vagaDTO);

        if (vaga.getIdentificacao() != null) {
            vaga.setIdentificacao(vaga.getIdentificacao().trim());
        }

        if (vagaDTO.getMoto() != null && vagaDTO.getMoto().getId() != null) {
            Moto moto = motoRepository.findById(vagaDTO.getMoto().getId())
                    .orElseThrow(() -> new RuntimeException("Moto não encontrada"));
            vaga.setMoto(moto);
        }

        if (vagaDTO.getPatioId() != null) {
            Patio patio = patioRepository.findById(vagaDTO.getPatioId())
                    .orElseThrow(() -> new RuntimeException("Pátio não encontrado"));
            vaga.setPatio(patio);
        }

        return VagaMapper.toDto(vagaRepository.save(vaga));
    }

    // Read by ID (entidade)
    @Transactional(readOnly = true)
    public Vaga readVagaById(UUID id) {
        Optional<Vaga> vaga = vagaRepository.findById(id);
        return vaga.orElse(null);
    }

    // Read all (paginado) — só cacheia quando for paginado
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = "vagas",
            condition  = "#pageable != null && #pageable.isPaged()",
            key        = "#pageable"
    )
    public Page<VagaDTO> readAllVagas(Pageable pageable) {
        Page<Vaga> page = vagaRepository.findAll(pageable);
        List<VagaDTO> dtoList = page.getContent()
                .stream()
                .map(VagaMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Read all (lista simples) — ideal para combos em formulários
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "vagasAll", key = "'ALL'")
    public List<VagaDTO> readAllVagas() {
        return vagaRepository.findAll()
                .stream()
                .map(VagaMapper::toDto)
                .toList();
    }

    // Update
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "vagas",    allEntries = true),
            @CacheEvict(cacheNames = "vagasAll", allEntries = true)
    })
    public VagaDTO updateVaga(UUID id, VagaDTO vagaDTO) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        vaga.setCoordenadaLat(vagaDTO.getCoordenadaLat());
        vaga.setCoordenadaLong(vagaDTO.getCoordenadaLong());
        vaga.setStatus(vagaDTO.getStatus());
        vaga.setIdentificacao(vagaDTO.getIdentificacao() != null ? vagaDTO.getIdentificacao().trim() : null);

        if (vagaDTO.getPatioId() != null) {
            Patio patio = patioRepository.findById(vagaDTO.getPatioId())
                    .orElseThrow(() -> new RuntimeException("Pátio não encontrado"));
            vaga.setPatio(patio);
        }

        if (vagaDTO.getMoto() != null && vagaDTO.getMoto().getId() != null) {
            Moto moto = motoRepository.findById(vagaDTO.getMoto().getId())
                    .orElseThrow(() -> new RuntimeException("Moto não encontrada"));
            vaga.setMoto(moto);
        } else {
            vaga.setMoto(null);
        }

        return VagaMapper.toDto(vagaRepository.save(vaga));
    }

    // Delete
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "vagas",    allEntries = true),
            @CacheEvict(cacheNames = "vagasAll", allEntries = true)
    })
    public void deleteVaga(UUID id) {
        vagaRepository.deleteById(id);
    }

    // Read by Patio and Status
    @Transactional(readOnly = true)
    public List<VagaDTO> readByPatioAndStatus(UUID patioId, StatusVaga status) {
        List<Vaga> vagas = vagaRepository.findByPatioIdAndStatus(patioId, status);
        return VagaMapper.toDtoList(vagas);
    }
}