package br.com.fiap.apisecurity.dto.usuario;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UsuarioPerfilResponse {

    private String nome_usuario;
    private CargoUsuario cargo; // NOVO

    public String getNome_usuario() { return nome_usuario; }
    public void setNome_usuario(String nome_usuario) { this.nome_usuario = nome_usuario; }

    public CargoUsuario getCargo() { return cargo; }
    public void setCargo(CargoUsuario cargo) { this.cargo = cargo; }
}

