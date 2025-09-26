package br.com.fiap.apisecurity.dto.usuario;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;

import java.util.UUID;

public class LoginResponse {

    private String idUsuario;         // UUID como string
    private String email;
    private CargoUsuario cargo;

    public LoginResponse(String idUsuario, String email, CargoUsuario cargo) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.cargo = cargo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CargoUsuario getCargo() {
        return cargo;
    }

    public void setCargo(CargoUsuario cargo) {
        this.cargo = cargo;
    }
}
