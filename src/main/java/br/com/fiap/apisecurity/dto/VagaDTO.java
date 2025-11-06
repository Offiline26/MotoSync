package br.com.fiap.apisecurity.dto;

import br.com.fiap.apisecurity.model.enums.StatusVaga;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class VagaDTO {

    private UUID id;

    @NotNull(message = "O status da vaga é obrigatório")
    private StatusVaga status;

    @NotNull(message = "O ID do pátio é obrigatório")
    private UUID patioId;

    // Novo: exibido na listagem (evita quebra no Thymeleaf)
    private String patioNome;

    private MotoDTO moto;

    @NotBlank(message = "A identificação é obrigatória")
    private String identificacao;

    public VagaDTO() {}

    public VagaDTO(UUID id, StatusVaga status,
                   UUID patioId, String patioNome, String identificacao, MotoDTO moto) {
        this.id = id;
        this.status = status;
        this.patioId = patioId;
        this.patioNome = patioNome;
        this.identificacao = identificacao;
        this.moto = moto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public StatusVaga getStatus() { return status; }
    public void setStatus(StatusVaga status) { this.status = status; }

    public UUID getPatioId() { return patioId; }
    public void setPatioId(UUID patioId) { this.patioId = patioId; }

    public String getPatioNome() { return patioNome; }
    public void setPatioNome(String patioNome) { this.patioNome = patioNome; }

    public MotoDTO getMoto() { return moto; }
    public void setMoto(MotoDTO moto) { this.moto = moto; }

    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }
}
