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
    public VagaDTO createVaga(VagaDTO vagaDTO) {
        Vaga vaga = VagaMapper.toEntity(vagaDTO);

        // Associa a moto se o DTO tiver moto
        if (vagaDTO.getMoto() != null && vagaDTO.getMoto().getId() != null) {
            Moto moto = motoRepository.findById(vagaDTO.getMoto().getId())
                    .orElseThrow(() -> new RuntimeException("Moto não encontrada"));
            vaga.setMoto(moto);
        }

        // Aqui também associar o Patio, se necessário
        if (vagaDTO.getPatioId() != null) {
            Patio patio = patioRepository.findById(vagaDTO.getPatioId())
                    .orElseThrow(() -> new RuntimeException("Pátio não encontrado"));
            vaga.setPatio(patio);
        }

        return VagaMapper.toDto(vagaRepository.save(vaga));
    }

    // Read by ID
    public Vaga readVagaById(UUID id) {
        Optional<Vaga> vaga = vagaRepository.findById(id);
        return vaga.orElse(null);  // Retorna a entidade Vaga
    }


    // Read all
    @Cacheable(value = "vagas", key = "#pageable")
    public Page<VagaDTO> readAllVagas(Pageable pageable) {
        Page<Vaga> page = vagaRepository.findAll(pageable);
        List<VagaDTO> dtoList = page.getContent()
                .stream()
                .map(VagaMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    public VagaDTO updateVaga(UUID id, VagaDTO vagaDTO) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        vaga.setCoordenadaLat(vagaDTO.getCoordenadaLat());
        vaga.setCoordenadaLong(vagaDTO.getCoordenadaLong());
        vaga.setStatus(vagaDTO.getStatus());

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
            vaga.setMoto(null); // permite remover a moto da vaga
        }

        return VagaMapper.toDto(vagaRepository.save(vaga));
    }


    // Delete
    @Transactional
    public void deleteVaga(UUID id) {
        vagaRepository.deleteById(id);
    }

    // Read by Patio and Status
    public List<VagaDTO> readByPatioAndStatus(UUID patioId, StatusVaga status) {
        // Aqui, você pode realizar a lógica de filtragem baseado no ID do Patio e Status
        List<Vaga> vagas = vagaRepository.findByPatioIdAndStatus(patioId, status); // Alteração no código para usar o Patio ID
        return VagaMapper.toDtoList(vagas);
    }
}
