package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.mapper.PatioMapper;
import br.com.fiap.apisecurity.model.Patio;
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
public class PatioService {

    private final PatioRepository patioRepository;
    private final VagaRepository vagaRepository;

    @Autowired
    public PatioService(PatioRepository patioRepository, VagaRepository vagaRepository) {
        this.patioRepository = patioRepository;
        this.vagaRepository = vagaRepository;
    }


    // Create
    @Transactional
    @CacheEvict(value = "patios:list", allEntries = true)
    public PatioDTO createPatio(PatioDTO patioDTO) {
        Patio patio = PatioMapper.toEntity(patioDTO);
        return PatioMapper.toDto(patioRepository.save(patio));
    }

    // Read by ID
    @Cacheable(value = "patios:item", key = "#id")
    public PatioDTO readPatioById(UUID id) {
        return patioRepository.findById(id)
                .map(PatioMapper::toDto)
                .orElse(null);
    }

    public Optional<Patio> readPatioEntityById(UUID id) {
        return patioRepository.findById(id);
    }


    // Read by cidade
    public List<PatioDTO> readByCidade(String cidade) {
        List<Patio> patios = patioRepository.findByCidade(cidade);
        return PatioMapper.toDtoList(patios);
    }

    // Read by nome
    public PatioDTO readByNome(String nome) {
        Patio patio = patioRepository.findByNome(nome);
        return PatioMapper.toDto(patio);
    }

    // Read all
    @Cacheable(
            cacheNames = "patios:list",
            condition  = "#pageable != null && #pageable.isPaged()",
            key        = "#pageable"
    )
    public Page<PatioDTO> readAllPatios(Pageable pageable) {
        Page<Patio> page = patioRepository.findAll(pageable);
        List<PatioDTO> dtoList = page.getContent()
                .stream()
                .map(PatioMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "patios:item", key = "#id"),
            @CacheEvict(value = "patios:list", allEntries = true)
    })
    public PatioDTO updatePatio(UUID id, PatioDTO patioDTO) {
        Patio patio = PatioMapper.toEntity(patioDTO);
        patio.setId(id);
        return PatioMapper.toDto(patioRepository.save(patio));
    }

    // Delete
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "patios:item", key = "#id"),
            @CacheEvict(value = "patios:list", allEntries = true)
    })
    public void inativarPatio(UUID id) {
        var patio = patioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pátio não encontrado."));

        long qtdVagas = vagaRepository.countByPatioId(id);
        if (qtdVagas > 0) {
            throw new IllegalStateException(
                    "Não é possível inativar este pátio: existem " + qtdVagas +
                            " vaga(s) vinculada(s). Remova as vagas antes de prosseguir."
            );
        }

        patioRepository.delete(patio);
    }

    public List<Patio> findAllEntities() {
        return patioRepository.findAll();
    }


    public Optional<Patio> findById(UUID id) {
        return patioRepository.findById(id);
    }
}


