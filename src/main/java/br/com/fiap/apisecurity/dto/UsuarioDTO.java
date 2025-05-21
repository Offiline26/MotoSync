package br.com.fiap.apisecurity.dto;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class UsuarioDTO {


    private UUID id;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank
    private String senha;

    @NotNull(message = "O cargo é obrigatório")
    private CargoUsuario cargo;

    public UsuarioDTO() {}

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UsuarioDTO(UUID id, String email, String senha, CargoUsuario cargo) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.cargo = cargo;
    }

    // getters e setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

