package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.SensorDTO;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Sensor;

import java.util.List;
import java.util.stream.Collectors;

public final class SensorMapper {

    public static SensorDTO toDto(Sensor sensor) {
        if (sensor == null) return null;
        return new SensorDTO(
                sensor.getId(),
                sensor.getCodigoUnico(),
                sensor.getStatus(),
                sensor.getMoto() != null ? sensor.getMoto().getId() : null
        );
    }

    public static Sensor toEntity(SensorDTO dto, Moto moto) {
        if (dto == null || moto == null) return null;

        Sensor sensor = new Sensor();
        sensor.setId(dto.getId());
        sensor.setCodigoUnico(dto.getCodigoUnico());
        sensor.setStatus(dto.getStatus());
        sensor.setMoto(moto); // ENTIDADE, não DTO

        return sensor;
    }

    public static List<SensorDTO> toDtoList(List<Sensor> sensores) {
        return sensores.stream().map(SensorMapper::toDto).collect(Collectors.toList());
    }
}


