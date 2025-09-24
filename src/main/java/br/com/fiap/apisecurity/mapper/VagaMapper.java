package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.model.Vaga;

import java.util.List;
import java.util.stream.Collectors;

public final class VagaMapper {

    private VagaMapper() {}

    public static VagaDTO toDto(Vaga vaga) {
        if (vaga == null) return null;
        return new VagaDTO(
                vaga.getId(),
                vaga.getCoordenadaLat(),
                vaga.getCoordenadaLong(),
                vaga.getStatus(),
                vaga.getPatio() != null ? vaga.getPatio().getId() : null,
                vaga.getIdentificacao(),
                MotoMapper.toDto(vaga.getMoto())
        );
    }

    public static Vaga toEntity(VagaDTO dto) {
        if (dto == null) return null;
        Vaga vaga = new Vaga();
        vaga.setId(dto.getId());
        vaga.setCoordenadaLat(dto.getCoordenadaLat());
        vaga.setCoordenadaLong(dto.getCoordenadaLong());
        vaga.setStatus(dto.getStatus());
        vaga.setIdentificacao(dto.getIdentificacao());
        return vaga;
    }

    public static List<VagaDTO> toDtoList(List<Vaga> vagas) {
        return vagas.stream()
                .map(VagaMapper::toDto)
                .collect(Collectors.toList());
    }
}
