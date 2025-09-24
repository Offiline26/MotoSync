package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.mapper.MotoMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.model.enums.StatusMoto;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import br.com.fiap.apisecurity.repository.MotoRepository;
import br.com.fiap.apisecurity.repository.VagaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MotoService {

    private final MotoRepository motoRepository;
    private final VagaRepository vagaRepository;

    @Autowired
    public MotoService(MotoRepository motoRepository, VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
        this.motoRepository = motoRepository;
    }

    @Transactional
    @Caching(
            put = { @CachePut(cacheNames="motosById", key="#result.id") },
            evict = {
                    @CacheEvict(cacheNames="motosList", allEntries=true),
                    @CacheEvict(cacheNames="motosListAtivas", allEntries=true)
            }
    )
    public MotoDTO createMoto(MotoDTO dto) {
        Moto moto = MotoMapper.toEntity(dto);

        if (dto.getVagaId() != null) {
            // não deixa ocupar vaga já ocupada
            if (motoRepository.existsByVagaId(dto.getVagaId())) {
                throw new IllegalStateException("Vaga já está ocupada");
            }
            moto.setVagaId(dto.getVagaId());
        }

        Moto saved = motoRepository.save(moto);

        // ATUALIZA TB_VAGA.MOTO_ID
        if (saved.getVagaId() != null) {
            Vaga vaga = vagaRepository.findById(saved.getVagaId())
                    .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
            if (vaga.getMoto() != null) throw new IllegalStateException("Vaga já está ocupada");
            vaga.setMoto(saved);
            vaga.setStatus(StatusVaga.OCUPADA);
            vagaRepository.save(vaga);
        }

        return MotoMapper.toDto(saved);
    }

    // Read by ID
    @Transactional
    @Cacheable(cacheNames = "motosById", key = "#id")
    public MotoDTO readMotoById(UUID id) {
        var moto = motoRepository.findById(id).orElse(null);
        if (moto == null) return null;
        String ident = null;
        if (moto.getVagaId() != null) {
            ident = vagaRepository.findById(moto.getVagaId())
                    .map(Vaga::getIdentificacao).orElse(null);
        }
        return MotoMapper.toDto(moto, ident);
    }

    @Transactional
    @Cacheable(cacheNames = "motosById", key = "#id")
    // Para RegistroService ou uso interno
    public Moto readMotoByIdEntity(UUID id) {
        return motoRepository.findById(id).orElse(null);
    }

    // Read all
    @Transactional
    @Cacheable(cacheNames = "motosList",
            condition = "#pageable != null && #pageable.paged",
            key = "T(java.lang.String).format('%s_%s_%s', #pageable.pageNumber, #pageable.pageSize, #pageable.sort)")
    public Page<MotoDTO> readAllMotos(Pageable pageable) {
        Page<Moto> page = motoRepository.findAll(pageable);

        // pega os IDs de vagas que aparecem na página
        var ids = page.getContent().stream()
                .map(Moto::getVagaId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        // carrega as vagas e monta mapa id -> identificacao
        var idToIdent = ids.isEmpty() ? java.util.Collections.<UUID,String>emptyMap()
                : vagaRepository.findAllById(ids).stream()
                .collect(java.util.stream.Collectors.toMap(
                        Vaga::getId,
                        v -> v.getIdentificacao()
                ));

        // mapeia as motos para DTO já com a identificacao
        var content = page.getContent().stream()
                .map(m -> MotoMapper.toDto(m, idToIdent.get(m.getVagaId())))
                .toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    @Caching(
            put = { @CachePut(cacheNames="motosById", key="#result.id") },
            evict = {
                    @CacheEvict(cacheNames="motosList", allEntries=true),
                    @CacheEvict(cacheNames="motosListAtivas", allEntries=true)
            }
    )
    public MotoDTO updateMoto(UUID id, MotoDTO dto) {
        Moto atual = motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        // placa não deve ser editável (mantém a atual)
        // placa permanece a mesma (não editável)
        atual.setStatus(dto.getStatus());

        UUID novaVaga = dto.getVagaId();
        UUID vagaAnterior = atual.getVagaId();

        // mudou a vaga?
        if (!Objects.equals(vagaAnterior, novaVaga)) {

            // libera a vaga anterior
            if (vagaAnterior != null) {
                vagaRepository.findById(vagaAnterior).ifPresent(v -> {
                    v.setMoto(null);
                    v.setStatus(StatusVaga.LIVRE);
                    vagaRepository.save(v);
                });
            }

            // ocupa a nova vaga
            if (novaVaga != null) {
                Vaga vNova = vagaRepository.findById(novaVaga)
                        .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
                if (vNova.getMoto() != null && !vNova.getMoto().getId().equals(id)) {
                    throw new IllegalStateException("Vaga já está ocupada");
                }
                vNova.setMoto(atual);
                vNova.setStatus(StatusVaga.OCUPADA);
                vagaRepository.save(vNova);
            }

            atual.setVagaId(novaVaga);
        }

        Moto salvo = motoRepository.save(atual);
        return MotoMapper.toDto(salvo);
    }

    // Delete
    @Transactional
    @Caching(
            put = { @CachePut(cacheNames="motosById", key="#result.id") },
            evict = {
                    @CacheEvict(cacheNames="motosList", allEntries=true),
                    @CacheEvict(cacheNames="motosListAtivas", allEntries=true)
            }
    ) // limpa o cache da moto; a lista será recarregada
    public void deleteMoto(UUID id) {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        UUID vagaId = moto.getVagaId();
        if (vagaId != null) {
            vagaRepository.findById(vagaId).ifPresent(v -> {
                v.setMoto(null);
                v.setStatus(StatusVaga.LIVRE);
                vagaRepository.save(v);
            });
            moto.setVagaId(null);
        }

        moto.setStatus(StatusMoto.INATIVADA);
        motoRepository.save(moto);
    }

    public Moto readByPlaca(String placa) {
        return motoRepository.findByPlacaIgnoreCase(placa);
    }

    // MotoService.java
    @Cacheable(cacheNames = "motosListAtivas",
            key = "T(java.lang.String).format('%s_%s_%s', #pageable.pageNumber, #pageable.pageSize, #pageable.sort)")
    public Page<MotoDTO> readAllMotosAtivas(Pageable pageable) {
        var page = motoRepository.findByStatusNot(StatusMoto.INATIVADA, pageable);
        var dtoList = page.getContent().stream().map(MotoMapper::toDto).toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    public MotoDTO readByPlacaDto(String placa) {
        var moto = motoRepository.findByPlacaIgnoreCase(placa);
        if (moto == null) return null;
        String ident = null;
        if (moto.getVagaId() != null) {
            ident = vagaRepository.findById(moto.getVagaId())
                    .map(Vaga::getIdentificacao).orElse(null);
        }
        return MotoMapper.toDto(moto, ident);
    }


}



