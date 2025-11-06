package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.LeitorDTO;
import br.com.fiap.apisecurity.model.Leitor;

import java.util.List;
import java.util.stream.Collectors;

public final class LeitorMapper {

    private LeitorMapper() {}

    // Converte de Entidade para DTO
    public static LeitorDTO toDto(Leitor leitor) {
        if (leitor == null) return null;
        return new LeitorDTO(
                leitor.getId(),
                leitor.getTipo(),
                leitor.getPatio() != null ? leitor.getPatio().getId() : null,
                leitor.getVaga() != null ? leitor.getVaga().getId() : null
        );
    }

    public static Leitor toEntity(LeitorDTO dto) {
        if (dto == null) return null;
        Leitor leitor = new Leitor();
        leitor.setId(dto.getId());
        leitor.setTipo(dto.getTipo());
        return leitor;
    }

    public static List<LeitorDTO> toDtoList(List<Leitor> leitores) {
        return leitores.stream()
                .map(LeitorMapper::toDto)
                .collect(Collectors.toList());
    }
}

