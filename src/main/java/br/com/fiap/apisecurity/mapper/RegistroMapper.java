package br.com.fiap.apisecurity.mapper;


import br.com.fiap.apisecurity.dto.RegistroDTO;
import br.com.fiap.apisecurity.model.Registro;

import java.util.List;
import java.util.stream.Collectors;

public final class RegistroMapper {

    private RegistroMapper() {}

    // Converte de Entidade para DTO
    public static RegistroDTO toDto(Registro registro) {
        if (registro == null) return null;
        return new RegistroDTO(
                registro.getId(),
                registro.getMoto() != null ? registro.getMoto().getId() : null,
                registro.getSensor() != null ? registro.getSensor().getId() : null,
                registro.getLeitor() != null ? registro.getLeitor().getId() : null,
                registro.getTipo(),
                registro.getDataHora()
        );
    }

    // Converte de DTO para Entidade
    public static Registro toEntity(RegistroDTO dto) {
        if (dto == null) return null;
        Registro registro = new Registro();
        registro.setId(dto.getId());
        registro.setTipoMovimentacao(dto.getTipo());
        registro.setDataHora(dto.getDataHora());
        // As associações de Moto, Sensor e Leitor devem ser feitas no Service ou Controller
        return registro;
    }

    // Converte uma lista de entidades para DTOs
    public static List<RegistroDTO> toDtoList(List<Registro> registros) {
        return registros.stream()
                .map(RegistroMapper::toDto)
                .collect(Collectors.toList());
    }
}


