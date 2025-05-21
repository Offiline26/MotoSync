package br.com.fiap.apisecurity.dto;

import br.com.fiap.apisecurity.model.enums.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegistroDTO {

    private UUID id;

    @NotNull(message = "O ID da moto é obrigatório")
    private UUID motoId;

    @NotNull(message = "O ID do sensor é obrigatório")
    private UUID sensorId;

    @NotNull(message = "O ID do leitor é obrigatório")
    private UUID leitorId;

    @NotNull(message = "O tipo de movimentação é obrigatório")
    private TipoMovimentacao tipo;

    @NotNull(message = "A data e hora da movimentação são obrigatórias")
    private LocalDateTime dataHora;

    public RegistroDTO() {}

    public RegistroDTO(UUID id, UUID motoId, UUID sensorId, UUID leitorId, TipoMovimentacao tipo, LocalDateTime dataHora) {
        this.id = id;
        this.motoId = motoId;
        this.sensorId = sensorId;
        this.leitorId = leitorId;
        this.tipo = tipo;
        this.dataHora = dataHora;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMotoId() {
        return motoId;
    }

    public void setMotoId(UUID motoId) {
        this.motoId = motoId;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public UUID getLeitorId() {
        return leitorId;
    }

    public void setLeitorId(UUID leitorId) {
        this.leitorId = leitorId;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
