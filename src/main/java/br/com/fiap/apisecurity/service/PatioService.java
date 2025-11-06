package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.controller.usuario.Authz;
import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.mapper.PatioMapper;
import br.com.fiap.apisecurity.model.Patio;
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
public class PatioService {

    private final PatioRepository patioRepository;
    private final VagaRepository vagaRepository;
    private final Authz authz;

    @Autowired
    public PatioService(PatioRepository patioRepository,
                        VagaRepository vagaRepository,
                        Authz authz) {
        this.patioRepository = patioRepository;
        this.vagaRepository  = vagaRepository;
        this.authz = authz;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames="patios",    allEntries = true),
            @CacheEvict(cacheNames="patiosAll", allEntries = true)
    })
    public PatioDTO createPatio(PatioDTO dto) {
        if (!authz.isAdmin()) {
            throw new SecurityException("Apenas ADMIN pode criar pátios.");
        }
        Patio entity = PatioMapper.toEntity(dto);
        Patio saved = patioRepository.save(entity);
        return PatioMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames="patios", key="#id")
    public PatioDTO readPatioById(UUID id) {
        Patio e = patioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + id));
        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (!id.equals(userPatio)) {
                throw new SecurityException("Operador só pode acessar o próprio pátio.");
            }
        }
        return PatioMapper.toDto(e);
    }

    @Transactional(readOnly = true)
    public List<PatioDTO> readByCidade(String cidade) {
        if (authz.isAdmin()) {
            return patioRepository.findByCidadeContainingIgnoreCase(cidade)
                    .stream().map(PatioMapper::toDto).toList();
        }
        UUID userPatio = authz.currentUserPatioIdOrThrow();
        return patioRepository.findById(userPatio)
                .filter(p -> p.getCidade()!=null && p.getCidade().toLowerCase().contains(cidade.toLowerCase()))
                .map(PatioMapper::toDto).stream().toList();
    }

    @Transactional(readOnly = true)
    public PatioDTO readByNome(String nome) {
        if (authz.isAdmin()) {
            return patioRepository.findByNomeIgnoreCase(nome)
                    .map(PatioMapper::toDto)
                    .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado pelo nome: " + nome));
        }
        UUID userPatio = authz.currentUserPatioIdOrThrow();
        Patio p = patioRepository.findById(userPatio)
                .orElseThrow(() -> new EntityNotFoundException("Pátio do operador não encontrado."));
        if (p.getNome()!=null && p.getNome().equalsIgnoreCase(nome)) {
            return PatioMapper.toDto(p);
        }
        throw new SecurityException("Operador só pode consultar o próprio pátio.");
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames="patios",
            key="(#pageable != null && #pageable.isPaged()) ? \n" +
                    "        (#pageable.pageNumber + ':' + #pageable.pageSize + ':' + (#pageable.sort != null ? #pageable.sort : 'UNSORTED')) \n" +
                    "        : 'UNPAGED'",
            condition="@authz.isAdmin()"
    )
    public Page<PatioDTO> readAllPatios(Pageable pageable) {
        if (authz.isAdmin()) {
            return patioRepository.findAll(pageable).map(PatioMapper::toDto);
        }

        UUID userPatio = authz.currentUserPatioIdOrThrow();
        return patioRepository.findById(userPatio)
                .map(p -> new PageImpl<>(List.of(PatioMapper.toDto(p)), pageable, 1L))
                .orElseGet(() -> new PageImpl<>(List.of(), pageable, 0L));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames="patios",    allEntries = true),
            @CacheEvict(cacheNames="patiosAll", allEntries = true)
    })
    public PatioDTO updatePatio(UUID id, PatioDTO dto) {
        Patio patio = patioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + id));

        if (!authz.isAdmin()) {
            UUID userPatio = authz.currentUserPatioIdOrThrow();
            if (!id.equals(userPatio)) {
                throw new SecurityException("Operador só pode atualizar o próprio pátio.");
            }
        }

        PatioMapper.apply(dto, patio);
        Patio saved = patioRepository.save(patio);
        return PatioMapper.toDto(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames="patios",    allEntries = true),
            @CacheEvict(cacheNames="patiosAll", allEntries = true)
    })
    public void inativarPatio(UUID id) {
        if (!authz.isAdmin()) {
            throw new SecurityException("Apenas ADMIN pode inativar/remover pátios.");
        }

        Patio patio = patioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + id));

        long qtdVagas = vagaRepository.countByPatio_Id(id);
        if (qtdVagas > 0) {
            throw new IllegalStateException(
                    "Não é possível inativar este pátio: existem " + qtdVagas + " vaga(s) vinculada(s)."
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


