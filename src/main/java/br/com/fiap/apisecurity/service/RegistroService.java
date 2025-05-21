package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.RegistroDTO;
import br.com.fiap.apisecurity.mapper.RegistroMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Registro;
import br.com.fiap.apisecurity.model.enums.TipoMovimentacao;
import br.com.fiap.apisecurity.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RegistroService {

    private final RegistroRepository registroRepository;

    @Autowired
    public RegistroService(RegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    // Create
    @Transactional
    @CachePut(value = "registros", key = "#result.id")
    public RegistroDTO createRegistro(RegistroDTO registroDTO) {
        Registro registro = RegistroMapper.toEntity(registroDTO);
        return RegistroMapper.toDto(registroRepository.save(registro));
    }

    // Read by ID
    @Cacheable(value = "registros", key = "#id")
    public RegistroDTO readRegistroById(UUID id) {
        Registro registro = registroRepository.findById(id).orElse(null);
        return RegistroMapper.toDto(registro);
    }

    // Read by Moto
    public List<RegistroDTO> readByMoto(Moto moto) {
        List<Registro> registros = registroRepository.findByMoto(moto);
        return RegistroMapper.toDtoList(registros);
    }

    // Read by Tipo
    public List<RegistroDTO> readByMotoAndTipo(Moto moto, TipoMovimentacao tipo) {
        List<Registro> registros = registroRepository.findByMotoAndTipo(moto, tipo);
        return RegistroMapper.toDtoList(registros);
    }

    // Read by Periodo
    public List<RegistroDTO> readByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Registro> registros = registroRepository.findByDataHoraBetween(inicio, fim);
        return RegistroMapper.toDtoList(registros);
    }

    // Read all
    @Cacheable(value = "registros", key = "#pageable")
    public Page<RegistroDTO> readAllRegistros(Pageable pageable) {
        Page<Registro> page = registroRepository.findAll(pageable);
        List<RegistroDTO> dtoList = page.getContent()
                .stream()
                .map(RegistroMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    @CachePut(value = "registros", key = "#result.id")
    public RegistroDTO updateRegistro(UUID id, RegistroDTO registroDTO) {
        Registro registro = RegistroMapper.toEntity(registroDTO);
        registro.setId(id);
        return RegistroMapper.toDto(registroRepository.save(registro));
    }

    // Delete
    @Transactional
    @CacheEvict(value = "registros", key = "#id")
    public void deleteRegistro(UUID id) {
        registroRepository.deleteById(id);
    }
}


