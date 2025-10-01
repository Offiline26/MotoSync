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
        if (dto.getVagaId() == null) {
            throw new IllegalArgumentException("Selecione uma vaga para cadastrar a moto.");
        }
        var moto = MotoMapper.toEntity(dto);
        // primeiro salva a moto para ter o ID (se precisar)
        moto = motoRepository.save(moto);

        moverMotoDePara(moto, dto.getVagaId());
        moto = motoRepository.save(moto);

        return MotoMapper.toDto(moto);
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
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        if (dto.getStatus() == StatusMoto.INATIVADA) {
            throw new IllegalArgumentException("Status INATIVADA só via exclusão.");
        }
        if (dto.getVagaId() == null) {
            throw new IllegalArgumentException("Selecione uma vaga para a moto.");
        }

        moverMotoDePara(moto, dto.getVagaId());  // garante ordem correta
        moto.setStatus(dto.getStatus());
        return MotoMapper.toDto(motoRepository.save(moto));
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
    public void inativarMoto(UUID id) {
        var moto = motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        UUID vagaAtualId = moto.getVagaId();

        // solta a vaga atual (se houver)
        if (vagaAtualId != null) {
            vagaRepository.findById(vagaAtualId).ifPresent(v -> {
                v.setMoto(null);
                v.setStatus(StatusVaga.LIVRE);
                vagaRepository.save(v);
            });
            moto.setVagaId(null);
        }

        // marca a moto como inativada
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

    private void moverMotoDePara(Moto moto, UUID novaVagaId) {
        UUID antigaId = moto.getVagaId();

        // Se não mudou de vaga, só garante consistência
        if (java.util.Objects.equals(antigaId, novaVagaId)) {
            Vaga mesma = vagaRepository.findById(novaVagaId)
                    .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));
            if (mesma.getMoto() == null) {
                mesma.setMoto(moto);
                mesma.setStatus(StatusVaga.OCUPADA);
                vagaRepository.save(mesma);
            }
            return;
        }

        // 1) Libera a vaga antiga e FLUSH (evita ORA-00001 na UNIQUE(moto_id))
        if (antigaId != null) {
            Vaga antiga = vagaRepository.findById(antigaId).orElse(null);
            if (antiga != null && (antiga.getMoto() == null
                    || antiga.getMoto().getId().equals(moto.getId()))) {
                antiga.setMoto(null);
                antiga.setStatus(StatusVaga.LIVRE);
                vagaRepository.save(antiga);
                vagaRepository.flush();   // <-- importante
            }
        }

        // 2) Ocupa a nova
        Vaga nova = vagaRepository.findById(novaVagaId)
                .orElseThrow(() -> new EntityNotFoundException("Vaga não encontrada"));

        if (nova.getMoto() != null && !nova.getMoto().getId().equals(moto.getId())) {
            throw new IllegalStateException("Vaga já está ocupada.");
        }
        nova.setMoto(moto);
        nova.setStatus(StatusVaga.OCUPADA);
        vagaRepository.save(nova);

        moto.setVagaId(nova.getId());
    }


}



