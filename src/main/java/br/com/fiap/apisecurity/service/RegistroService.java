package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.RegistroDTO;
import br.com.fiap.apisecurity.mapper.RegistroMapper;
import br.com.fiap.apisecurity.model.Leitor;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Registro;
import br.com.fiap.apisecurity.model.enums.TipoMovimentacao;
import br.com.fiap.apisecurity.repository.LeitorRepository;
import br.com.fiap.apisecurity.repository.MotoRepository;
import br.com.fiap.apisecurity.repository.RegistroRepository;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistroService {

    private final RegistroRepository registroRepository;
    private final MotoRepository motoRepository;
    private final LeitorRepository leitorRepository;

    @Autowired
    public RegistroService(RegistroRepository registroRepository, MotoRepository motoRepository, LeitorRepository leitorRepository) {
        this.registroRepository = registroRepository;
        this.motoRepository = motoRepository;
        this.leitorRepository = leitorRepository;
    }

    // Create
    @Transactional
    public RegistroDTO createRegistro(RegistroDTO dto) {
        Moto moto = motoRepository.findById(dto.getMotoId()).orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));
        Leitor leitor = leitorRepository.findById(dto.getLeitorId()).orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        Registro registro = new Registro();
        registro.setId(dto.getId());
        registro.setDataHora(dto.getDataHora());
        registro.setTipo(dto.getTipo());
        registro.setMoto(moto);
        registro.setLeitor(leitor);

        return RegistroMapper.toDto(registroRepository.save(registro));
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

    public List<RegistroDTO> readAll() {
        return RegistroMapper.toDtoList(registroRepository.findAll());
    }

    public List<RegistroDTO> readByMotoId(UUID motoId) {
        Moto moto = motoRepository.findById(motoId).orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));
        return RegistroMapper.toDtoList(registroRepository.findByMoto(moto));
    }

    public List<RegistroDTO> readByMotoIdAndTipo(UUID motoId, TipoMovimentacao tipo) {
        Moto moto = motoRepository.findById(motoId).orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));
        return RegistroMapper.toDtoList(registroRepository.findByMotoAndTipo(moto, tipo));
    }

    public List<RegistroDTO> readByPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return RegistroMapper.toDtoList(registroRepository.findByDataHoraBetween(inicio, fim));
    }

    // Update
    @Transactional
    @CachePut(value = "registros", key = "#result.id")
    public RegistroDTO updateRegistro(UUID id, RegistroDTO dto) {
        Optional<Registro> optionalRegistro = registroRepository.findById(id);
        if (optionalRegistro.isEmpty()) return null;

        Registro registro = optionalRegistro.get();

        Moto moto = motoRepository.findById(dto.getMotoId()).orElseThrow(() -> new EntityNotFoundException("Moto não encontrada"));
        Leitor leitor = leitorRepository.findById(dto.getLeitorId()).orElseThrow(() -> new EntityNotFoundException("Leitor não encontrado"));

        registro.setDataHora(dto.getDataHora());
        registro.setTipo(dto.getTipo());
        registro.setMoto(moto);
        registro.setLeitor(leitor);

        return RegistroMapper.toDto(registroRepository.save(registro));
    }

    // Delete
    @Transactional
    @CacheEvict(value = "registros", key = "#id")
    public void deleteRegistro(UUID id) {
        registroRepository.deleteById(id);
    }
}


