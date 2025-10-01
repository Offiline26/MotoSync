package br.com.fiap.apisecurity.mapper;


import br.com.fiap.apisecurity.dto.RegistroDTO;
import br.com.fiap.apisecurity.model.Leitor;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Registro;

import java.util.List;
import java.util.stream.Collectors;

public final class RegistroMapper {

    private RegistroMapper() {}

    public static RegistroDTO toDto(Registro registro) {
        if (registro == null) return null;
        return new RegistroDTO(
                registro.getId(),
                registro.getMoto() != null ? registro.getMoto().getId() : null,
                registro.getLeitor() != null ? registro.getLeitor().getId() : null,
                registro.getTipo(),
                registro.getDataHora()
        );
    }

    public static Registro toEntity(RegistroDTO dto, Moto moto, Leitor leitor) {
        if (dto == null) return null;
        Registro registro = new Registro();
        registro.setId(dto.getId());
        registro.setDataHora(dto.getDataHora());
        registro.setTipo(dto.getTipo());
        registro.setMoto(moto);
        registro.setLeitor(leitor);
        return registro;
    }

    public static List<RegistroDTO> toDtoList(List<Registro> registros) {
        return registros.stream()
                .map(RegistroMapper::toDto)
                .collect(Collectors.toList());
    }
}


