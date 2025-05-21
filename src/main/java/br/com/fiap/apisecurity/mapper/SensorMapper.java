package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.SensorDTO;
import br.com.fiap.apisecurity.model.Sensor;

import java.util.List;
import java.util.stream.Collectors;

public final class SensorMapper {

    private SensorMapper() {}

    // Converte de Entidade para DTO
    public static SensorDTO toDto(Sensor sensor) {
        if (sensor == null) return null;
        return new SensorDTO(
                sensor.getId(),
                sensor.getCodigoUnico(),
                sensor.getStatus(),
                sensor.getMoto() != null ? sensor.getMoto().getId() : null
        );
    }

    // Converte de DTO para Entidade
    public static Sensor toEntity(SensorDTO dto) {
        if (dto == null) return null;
        Sensor sensor = new Sensor();
        sensor.setId(dto.getId());
        sensor.setCodigoUnico(dto.getCodigoUnico());
        sensor.setStatus(dto.getStatus());
        // A associação de Moto pode ser feita no Service ou Controller
        return sensor;
    }

    // Converte uma lista de entidades para DTOs
    public static List<SensorDTO> toDtoList(List<Sensor> sensores) {
        return sensores.stream()
                .map(SensorMapper::toDto)
                .collect(Collectors.toList());
    }
}


