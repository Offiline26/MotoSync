package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.Vaga;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class VagaMapper {

    private VagaMapper() {}

    public static VagaDTO toDto(Vaga vaga) {
        if (vaga == null) return null;

        VagaDTO dto = new VagaDTO();
        dto.setId(vaga.getId());
        dto.setStatus(vaga.getStatus());
        dto.setIdentificacao(vaga.getIdentificacao());

        if (vaga.getPatio() != null) {
            dto.setPatioId(vaga.getPatio().getId());
            dto.setPatioNome(vaga.getPatio().getNome());
        }

        dto.setMoto(MotoMapper.toDto(vaga.getMoto()));
        return dto;
    }

    public static Vaga toEntity(VagaDTO dto) {
        if (dto == null) return null;
        Vaga vaga = new Vaga();
        vaga.setId(dto.getId());
        vaga.setStatus(dto.getStatus());
        vaga.setIdentificacao(dto.getIdentificacao());
        return vaga;
    }

    public static List<VagaDTO> toDtoList(List<Vaga> vagas) {
        return vagas.stream().map(VagaMapper::toDto).collect(Collectors.toList());
    }

    public static void apply(VagaDTO dto, Vaga target) {
        if (dto == null || target == null) return;
        if (dto.getStatus()         != null) target.setStatus(dto.getStatus());
        if (dto.getIdentificacao()  != null) target.setIdentificacao(dto.getIdentificacao());
    }

    public static void apply(
            VagaDTO dto,
            Vaga target,
            Function<UUID, Patio> patioResolver
    ) {
        apply(dto, target); // atualiza campos simples
        if (dto != null && dto.getPatioId() != null && patioResolver != null) {
            Patio patio = patioResolver.apply(dto.getPatioId());
            target.setPatio(patio);
        }
    }
}
