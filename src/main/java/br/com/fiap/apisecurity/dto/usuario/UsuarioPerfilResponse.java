package br.com.fiap.apisecurity.dto.usuario;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;

/** Payload simples para exibir o perfil do usuário. */
public class UsuarioPerfilResponse {

    private String id;              // UUID como string
    private String email;
    private CargoUsuario cargo;

    public UsuarioPerfilResponse() {}

    public UsuarioPerfilResponse(String id, String email, CargoUsuario cargo) {
        this.id = id;
        this.email = email;
        this.cargo = cargo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public CargoUsuario getCargo() { return cargo; }
    public void setCargo(CargoUsuario cargo) { this.cargo = cargo; }
}
