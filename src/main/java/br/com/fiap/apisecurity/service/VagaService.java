package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.mapper.VagaMapper;
import br.com.fiap.apisecurity.model.StatusVaga;
import br.com.fiap.apisecurity.model.Vaga;
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

    @Autowired
    public VagaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    // Create
    @Transactional
    public VagaDTO createVaga(VagaDTO vagaDTO) {
        // Converte o DTO para a entidade
        Vaga vaga = VagaMapper.toEntity(vagaDTO);
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
        Vaga vaga = VagaMapper.toEntity(vagaDTO);
        vaga.setId(id);
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
