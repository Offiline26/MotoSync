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
        dto.setCoordenadaLat(vaga.getCoordenadaLat());
        dto.setCoordenadaLong(vaga.getCoordenadaLong());
        dto.setStatus(vaga.getStatus());
        dto.setIdentificacao(vaga.getIdentificacao());

        if (vaga.getPatio() != null) {
            dto.setPatioId(vaga.getPatio().getId());
            dto.setPatioNome(vaga.getPatio().getNome()); // útil pra view
        }

        dto.setMoto(MotoMapper.toDto(vaga.getMoto())); // mantém associação via DTO
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
        // Patio e Moto são setados no Service
        return vaga;
    }

    public static List<VagaDTO> toDtoList(List<Vaga> vagas) {
        return vagas.stream().map(VagaMapper::toDto).collect(Collectors.toList());
    }

    // >>> NOVO: update parcial por campos NÃO nulos (sem trocar pátio)
    public static void apply(VagaDTO dto, Vaga target) {
        if (dto == null || target == null) return;
        if (dto.getCoordenadaLat()  != null) target.setCoordenadaLat(dto.getCoordenadaLat());
        if (dto.getCoordenadaLong() != null) target.setCoordenadaLong(dto.getCoordenadaLong());
        if (dto.getStatus()         != null) target.setStatus(dto.getStatus());
        if (dto.getIdentificacao()  != null) target.setIdentificacao(dto.getIdentificacao());
        // Nota: não alteramos Moto aqui; isso fica a cargo do Service conforme suas regras.
    }

    // >>> NOVO: update parcial + possibilidade de atualizar o Pátio via resolver
    public static void apply(
            VagaDTO dto,
            Vaga target,
            Function<UUID, Patio> patioResolver
    ) {
        apply(dto, target); // atualiza campos simples
        if (dto != null && dto.getPatioId() != null && patioResolver != null) {
            Patio patio = patioResolver.apply(dto.getPatioId());
            // Se quiser forçar erro quando não achar, deixe o resolver lançar (como você fez no Service)
            target.setPatio(patio);
        }
    }
}
