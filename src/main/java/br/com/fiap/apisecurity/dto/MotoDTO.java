package br.com.fiap.apisecurity.dto;

import br.com.fiap.apisecurity.model.StatusMoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class MotoDTO {

    private UUID id;

    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 7, max = 8, message = "A placa deve ter entre 7 e 8 caracteres")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "A placa deve conter apenas letras maiúsculas, números e hífen")
    private String placa;

    @NotNull(message = "O status da moto é obrigatório")
    private StatusMoto status;

    public MotoDTO() {}

    public MotoDTO(UUID id, String placa, StatusMoto status) {
        this.id = id;
        this.placa = placa;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public StatusMoto getStatus() {
        return status;
    }

    public void setStatus(StatusMoto status) {
        this.status = status;
    }
}
