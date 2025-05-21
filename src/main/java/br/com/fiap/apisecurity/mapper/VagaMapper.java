package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.model.Vaga;

import java.util.List;
import java.util.stream.Collectors;

public final class VagaMapper {

    private VagaMapper() {}

    // Converte de Entidade para DTO
    public static VagaDTO toDto(Vaga vaga) {
        if (vaga == null) return null;
        return new VagaDTO(
                vaga.getId(),
                vaga.getCoordenadaLat(),
                vaga.getCoordenadaLong(),
                vaga.getStatus(),
                vaga.getPatio() != null ? vaga.getPatio().getId() : null
        );
    }

    // Converte de DTO para Entidade
    public static Vaga toEntity(VagaDTO dto) {
        if (dto == null) return null;
        Vaga vaga = new Vaga();
        vaga.setId(dto.getId());
        vaga.setCoordenadaLat(dto.getCoordenadaLat());
        vaga.setCoordenadaLong(dto.getCoordenadaLong());
        vaga.setStatus(dto.getStatus());
        // A associação de Pátio pode ser feita no Service ou Controller
        return vaga;
    }

    // Converte uma lista de entidades para DTOs
    public static List<VagaDTO> toDtoList(List<Vaga> vagas) {
        return vagas.stream()
                .map(VagaMapper::toDto)
                .collect(Collectors.toList());
    }
}
