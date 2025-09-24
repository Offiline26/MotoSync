package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.mapper.MotoMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.model.enums.StatusMoto;
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

import java.util.List;
import java.util.UUID;

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
            put   = { @CachePut(cacheNames = "motosById", key = "#result.id") },
            evict = { @CacheEvict(cacheNames = "motosList", allEntries = true) }
    )
    public MotoDTO createMoto(MotoDTO motoDTO) {
        Moto moto = MotoMapper.toEntity(motoDTO);

        if (motoDTO.getVagaId() != null) {
            UUID vagaId = motoDTO.getVagaId();
            Vaga vaga = vagaRepository.findById(vagaId)
                    .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));
            moto.setVagaId(vaga.getId());
        }

        return MotoMapper.toDto(motoRepository.save(moto));
    }

    // Read by ID
    @Cacheable(cacheNames = "motosById", key = "#id")
    // Para Controller (GET por ID)
    public MotoDTO readMotoById(UUID id) {
        Moto moto = motoRepository.findById(id).orElse(null);
        return (moto != null) ? MotoMapper.toDto(moto) : null;
    }

    @Cacheable(cacheNames = "motosById", key = "#id")
    // Para RegistroService ou uso interno
    public Moto readMotoByIdEntity(UUID id) {
        return motoRepository.findById(id).orElse(null);
    }

    // Read all
    @Cacheable(cacheNames = "motosList", condition = "#pageable != null && #pageable.paged")
    public Page<MotoDTO> readAllMotos(Pageable pageable) {
        Page<Moto> page = motoRepository.findByStatusNot(StatusMoto.INATIVADA, pageable);;
        List<MotoDTO> dtoList = page.getContent()
                .stream()
                .map(MotoMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    @Caching(
            put   = { @CachePut(cacheNames = "motosById", key = "#result.id") },
            evict = { @CacheEvict(cacheNames = "motosList", allEntries = true) }
    )
    public MotoDTO updateMoto(UUID id, MotoDTO motoDTO) {
        Moto moto = MotoMapper.toEntity(motoDTO);
        moto.setId(id);
        return MotoMapper.toDto(motoRepository.save(moto));
    }

    // Delete
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "motosById", key = "#id"),
                    @CacheEvict(cacheNames = "motosList", allEntries = true)
            }
    ) // limpa o cache da moto; a lista será recarregada
    public void deleteMoto(UUID id) {
        Moto moto = motoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));

        // Se a sua entidade armazena o ID da vaga:
        UUID vagaId = moto.getVagaId();
        if (vagaId != null) {
            vagaRepository.findById(vagaId).ifPresent(v -> {
                v.setMoto(null);
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
}



