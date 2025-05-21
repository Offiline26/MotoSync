package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.model.Patio;

import java.util.List;
import java.util.stream.Collectors;

public final class PatioMapper {

    private PatioMapper() {}

    // Converte de Entidade para DTO
    public static PatioDTO toDto(Patio patio) {
        if (patio == null) return null;
        return new PatioDTO(
                patio.getId(),
                patio.getNome(),
                patio.getRua(),
                patio.getNumero(),
                patio.getBairro(),
                patio.getCidade(),
                patio.getEstado(),
                patio.getPais()
        );
    }

    // Converte de DTO para Entidade
    public static Patio toEntity(PatioDTO dto) {
        if (dto == null) return null;
        Patio patio = new Patio();
        patio.setId(dto.getId());
        patio.setNome(dto.getNome());
        patio.setRua(dto.getRua());
        patio.setNumero(dto.getNumero());
        patio.setBairro(dto.getBairro());
        patio.setCidade(dto.getCidade());
        patio.setEstado(dto.getEstado());
        patio.setPais(dto.getPais());
        return patio;
    }

    // Converte uma lista de entidades para DTOs
    public static List<PatioDTO> toDtoList(List<Patio> patios) {
        return patios.stream()
                .map(PatioMapper::toDto)
                .collect(Collectors.toList());
    }
}


