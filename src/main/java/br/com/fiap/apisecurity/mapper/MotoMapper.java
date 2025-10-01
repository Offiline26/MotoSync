package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.model.Moto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class MotoMapper {

    private MotoMapper() {}

    public static MotoDTO toDto(Moto moto) {
        if (moto == null) return null;
        return new MotoDTO(
        moto.getId(),
        moto.getPlaca(),
        moto.getStatus(),
        moto.getVagaId());
    }

    // ajuda para setar a identificação já no DTO
    public static MotoDTO toDto(Moto m, String vagaIdent) {
        MotoDTO dto = toDto(m);
        if (dto != null) dto.setVagaIdentificacao(vagaIdent);
        return dto;
    }

    public static Moto toEntity(MotoDTO dto) {
        if (dto == null) return null;
        Moto moto = new Moto();
        moto.setId(dto.getId());
        moto.setPlaca(dto.getPlaca());
        moto.setStatus(dto.getStatus());
        moto.setVagaId(dto.getVagaId());
        return moto;
    }

    public static List<MotoDTO> toDtoList(List<Moto> motos) {
        return motos.stream()
                .map(MotoMapper::toDto)
                .collect(Collectors.toList());
    }

}



