package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.model.Vaga;

import java.util.List;
import java.util.stream.Collectors;

public final class VagaMapper {

    private VagaMapper() {}

    public static VagaDTO toDto(Vaga vaga) {
        if (vaga == null) return null;

        VagaDTO dto = new VagaDTO();
        dto.setId(vaga.getId());
        dto.setCoordenadaLat(vaga.getCoordenadaLat());
        dto.setCoordenadaLong(vaga.getCoordenadaLong());
        dto.setStatus(vaga.getStatus());
        dto.setIdentificacao(vaga.getIdentificacao());

        if (vaga.getPatio() != null) {
            dto.setPatioId(vaga.getPatio().getId());
            // novo: nome do pátio para a view
            dto.setPatioNome(vaga.getPatio().getNome());
        }

        // mantém associação da moto via DTO
        dto.setMoto(MotoMapper.toDto(vaga.getMoto()));
        return dto;
    }

    public static Vaga toEntity(VagaDTO dto) {
        if (dto == null) return null;
        Vaga vaga = new Vaga();
        vaga.setId(dto.getId());
        vaga.setCoordenadaLat(dto.getCoordenadaLat());
        vaga.setCoordenadaLong(dto.getCoordenadaLong());
        vaga.setStatus(dto.getStatus());
        vaga.setIdentificacao(dto.getIdentificacao());
        // Patio e Moto são setados no Service (carregados por id)
        return vaga;
    }

    public static List<VagaDTO> toDtoList(List<Vaga> vagas) {
        return vagas.stream().map(VagaMapper::toDto).collect(Collectors.toList());
    }
}
