package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.mapper.MotoMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.repository.MotoRepository;
import br.com.fiap.apisecurity.repository.VagaRepository;
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
public class MotoService {

    private final MotoRepository motoRepository;
    private final VagaRepository vagaRepository;

    @Autowired
    public MotoService(MotoRepository motoRepository, VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
        this.motoRepository = motoRepository;
    }

    // Create
    @Transactional
    @CachePut(value = "motos", key = "#result.id")
    public MotoDTO createMoto(MotoDTO motoDTO) {
        Moto moto = MotoMapper.toEntity(motoDTO);

        // A associação com a Vaga é feita apenas por ID (vagaId no DTO)
        if (motoDTO.getVagaId() != null) {
            UUID vagaId = motoDTO.getVagaId();
            Vaga vaga = vagaRepository.findById(vagaId)
                    .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));
            moto.setVagaId(vaga.getId());
        }

        return MotoMapper.toDto(motoRepository.save(moto));
    }

    // Read by ID
    @Cacheable(value = "motos", key = "#id")
    public Moto readMotoById(UUID id) {
        Optional<Moto> moto = motoRepository.findById(id);
        return moto.orElse(null);  // Retorna a entidade Moto
    }


    // Read all
    @Cacheable(value = "motos", key = "#pageable")
    public Page<MotoDTO> readAllMotos(Pageable pageable) {
        Page<Moto> page = motoRepository.findAll(pageable);
        List<MotoDTO> dtoList = page.getContent()
                .stream()
                .map(MotoMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    @CachePut(value = "motos", key = "#result.id")
    public MotoDTO updateMoto(UUID id, MotoDTO motoDTO) {
        Moto moto = MotoMapper.toEntity(motoDTO);
        moto.setId(id);
        return MotoMapper.toDto(motoRepository.save(moto));
    }

    // Delete
    @Transactional
    @CacheEvict(value = "motos", key = "#id")
    public void deleteMoto(UUID id) {
        motoRepository.deleteById(id);
    }

    public Moto readByPlaca(String placa) {
        return motoRepository.findByPlaca(placa);
    }
}


