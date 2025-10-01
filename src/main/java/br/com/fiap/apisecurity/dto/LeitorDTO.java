package br.com.fiap.apisecurity.dto;

import br.com.fiap.apisecurity.model.enums.TipoLeitor;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class LeitorDTO {

    private UUID id;

    @NotNull(message = "O tipo do leitor é obrigatório")
    private TipoLeitor tipo;

    @NotNull(message = "O ID do pátio é obrigatório")
    private UUID patioId;

    // A vagaId pode ser null, dependendo do tipo

    private UUID vagaId;

    public LeitorDTO() {}

    public LeitorDTO(UUID id, TipoLeitor tipo, UUID patioId, UUID vagaId) {
        this.id = id;
        this.tipo = tipo;
        this.patioId = patioId;
        this.vagaId = vagaId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TipoLeitor getTipo() {
        return tipo;
    }

    public void setTipo(TipoLeitor tipo) {
        this.tipo = tipo;
    }

    public UUID getPatioId() {
        return patioId;
    }

    public void setPatioId(UUID patioId) {
        this.patioId = patioId;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public void setVagaId(UUID vagaId) {
        this.vagaId = vagaId;
    }
}
