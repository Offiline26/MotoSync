package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.LeitorDTO;
import br.com.fiap.apisecurity.mapper.LeitorMapper;
import br.com.fiap.apisecurity.model.Leitor;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.TipoLeitor;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.repository.LeitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeitorService {

    private final LeitorRepository leitorRepository;

    @Autowired
    public LeitorService(LeitorRepository leitorRepository) {
        this.leitorRepository = leitorRepository;
    }

    // Create
    @Transactional
    @CachePut(value = "leitores", key = "#result.id")
    public LeitorDTO createLeitor(LeitorDTO leitorDTO) {
        Leitor leitor = LeitorMapper.toEntity(leitorDTO);
        return LeitorMapper.toDto(leitorRepository.save(leitor));
    }

    // Read by ID
    @Cacheable(value = "leitores", key = "#id")
    public LeitorDTO readLeitorById(UUID id) {
        Leitor leitor = leitorRepository.findById(id).orElse(null);
        return LeitorMapper.toDto(leitor);
    }

    // Read by tipo
    @Cacheable(value = "leitores", key = "'tipo-' + #tipo")
    public List<LeitorDTO> readByTipo(TipoLeitor tipo) {
        List<Leitor> leitores = leitorRepository.findByTipo(tipo);
        return LeitorMapper.toDtoList(leitores);
    }

    // Read by pátio
    @Cacheable(value = "leitores", key = "'patio-' + #patio.id")
    public List<LeitorDTO> readByPatio(Patio patio) {
        List<Leitor> leitores = leitorRepository.findByPatio(patio);
        return LeitorMapper.toDtoList(leitores);
    }

    // Read by vaga and tipo
    public Optional<LeitorDTO> readByVagaAndTipo(Vaga vaga, TipoLeitor tipo) {
        Optional<Leitor> leitorOpt = leitorRepository.findByVagaAndTipo(vaga, tipo);
        return leitorOpt.map(LeitorMapper::toDto);
    }

    // Read all
    @Cacheable(value = "leitores", key = "'all'")
    public Page<LeitorDTO> readAllLeitores(Pageable pageable) {
        Page<Leitor> page = leitorRepository.findAll(pageable);
        List<LeitorDTO> dtoList = page.getContent()
                .stream()
                .map(LeitorMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    @CachePut(value = "leitores", key = "#result.id")
    public LeitorDTO updateLeitor(UUID id, LeitorDTO leitorDTO) {
        Optional<Leitor> optionalLeitor = leitorRepository.findById(id);
        if (optionalLeitor.isEmpty()) {
            return null;
        }
        Leitor leitor = LeitorMapper.toEntity(leitorDTO);
        leitor.setId(id);
        return LeitorMapper.toDto(leitorRepository.save(leitor));
    }

    // Delete
    @Transactional
    @CacheEvict(value = "leitores", key = "#id")
    public void deleteLeitor(UUID id) {
        leitorRepository.deleteById(id);
    }
}

