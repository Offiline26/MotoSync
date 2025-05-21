package br.com.fiap.apisecurity.dto;

import br.com.fiap.apisecurity.model.enums.StatusSensor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class SensorDTO {

    private UUID id;

    @NotBlank(message = "O código único é obrigatório")
    @Size(min = 4, max = 50, message = "O código único deve ter entre 4 e 50 caracteres")
    private String codigoUnico;

    @NotNull(message = "O status do sensor é obrigatório")
    private StatusSensor status;

    @NotNull(message = "O ID da moto é obrigatório")
    private UUID motoId;

    public SensorDTO() {}

    public SensorDTO(UUID id, String codigoUnico, StatusSensor status, UUID motoId) {
        this.id = id;
        this.codigoUnico = codigoUnico;
        this.status = status;
        this.motoId = motoId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public StatusSensor getStatus() {
        return status;
    }

    public void setStatus(StatusSensor status) {
        this.status = status;
    }

    public UUID getMotoId() {
        return motoId;
    }

    public void setMotoId(UUID motoId) {
        this.motoId = motoId;
    }
}
