package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.mapper.PatioMapper;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.repository.PatioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Autowired
    public PatioService(PatioRepository patioRepository) {
        this.patioRepository = patioRepository;
    }


    // Create
    @Transactional
    @CachePut(value = "patios", key = "#result.id")
    public PatioDTO createPatio(PatioDTO patioDTO) {
        Patio patio = PatioMapper.toEntity(patioDTO);
        return PatioMapper.toDto(patioRepository.save(patio));
    }

    // Read by ID
    @Cacheable(value = "patios", key = "#id")
    public PatioDTO readPatioById(UUID id) {
        return patioRepository.findById(id)
                .map(PatioMapper::toDto)
                .orElse(null);
    }

    public Patio readPatioEntityById(UUID id) {
        return patioRepository.findById(id).orElse(null);
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
    @Cacheable(value = "patios", key = "#pageable")
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
    @CachePut(value = "patios", key = "#result.id")
    public PatioDTO updatePatio(UUID id, PatioDTO patioDTO) {
        Patio patio = PatioMapper.toEntity(patioDTO);
        patio.setId(id);
        return PatioMapper.toDto(patioRepository.save(patio));
    }

    // Delete
    @Transactional
    @CacheEvict(value = "patios", key = "#id")
    public void deletePatio(UUID id) {
        patioRepository.deleteById(id);
    }
}


