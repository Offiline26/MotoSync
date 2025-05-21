package br.com.fiap.apisecurity.dto;

import br.com.fiap.apisecurity.model.StatusVaga;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class VagaDTO {

    private UUID id;

    @NotNull(message = "A coordenada de latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "Latitude mínima é -90")
    @DecimalMax(value = "90.0", message = "Latitude máxima é 90")
    private double coordenadaLat;

    @NotNull(message = "A coordenada de longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "Longitude mínima é -180")
    @DecimalMax(value = "180.0", message = "Longitude máxima é 180")
    private double coordenadaLong;

    @NotNull(message = "O status da vaga é obrigatório")
    private StatusVaga status;

    @NotNull(message = "O ID do pátio é obrigatório")
    private UUID patioId;

    public VagaDTO() {}

    public VagaDTO(UUID id, double coordenadaLat, double coordenadaLong, StatusVaga status, UUID patioId) {
        this.id = id;
        this.coordenadaLat = coordenadaLat;
        this.coordenadaLong = coordenadaLong;
        this.status = status;
        this.patioId = patioId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getCoordenadaLat() {
        return coordenadaLat;
    }

    public void setCoordenadaLat(double coordenadaLat) {
        this.coordenadaLat = coordenadaLat;
    }

    public double getCoordenadaLong() {
        return coordenadaLong;
    }

    public void setCoordenadaLong(double coordenadaLong) {
        this.coordenadaLong = coordenadaLong;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga status) {
        this.status = status;
    }

    public UUID getPatioId() {
        return patioId;
    }

    public void setPatioId(UUID patioId) {
        this.patioId = patioId;
    }
}
