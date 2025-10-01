package br.com.fiap.apisecurity.dto.usuario;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;

import java.util.UUID;

public class RegisterRequest {
    private String email;
    private String password;
    private CargoUsuario cargo; // opcional, default OPERADOR_PATIO
    private UUID patioId;

    public UUID getPatioId() {
        return patioId;
    }

    public void setPatioId(UUID patioId) {
        this.patioId = patioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CargoUsuario getCargo() {
        return cargo;
    }

    public void setCargo(CargoUsuario cargo) {
        this.cargo = cargo;
    }
}
