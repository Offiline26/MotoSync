package br.com.fiap.apisecurity.dto.usuario;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;

import java.util.UUID;

public class UsuarioPerfilResponse {

    private String id;
    private String email;
    private CargoUsuario cargo;
    private UUID patioId;
    private String patioNome;

    public UsuarioPerfilResponse() {}

    public UsuarioPerfilResponse(String id, String email, CargoUsuario cargo, UUID patioId, String patioNome) {
        this.id = id;
        this.email = email;
        this.cargo = cargo;
        this.patioId = patioId;
        this.patioNome = patioNome;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public CargoUsuario getCargo() { return cargo; }
    public void setCargo(CargoUsuario cargo) { this.cargo = cargo; }

    public UUID getPatioId() { return patioId; }

    public void setPatioId(UUID patioId) { this.patioId = patioId; }

    public String getPatioNome() { return patioNome; }

    public void setPatioNome(String patioNome) { this.patioNome = patioNome; }
}
