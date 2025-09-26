package br.com.fiap.apisecurity.dto.usuario;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;

public class LoginResponse {

    private String token;
    private Integer idUsuario;
    private CargoUsuario cargo; // NOVO

    public LoginResponse(String token, Integer idUsuario, CargoUsuario cargo) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.cargo = cargo;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public CargoUsuario getCargo() { return cargo; }
    public void setCargo(CargoUsuario cargo) { this.cargo = cargo; }
}
