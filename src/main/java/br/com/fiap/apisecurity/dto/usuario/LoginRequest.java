package br.com.fiap.apisecurity.dto.usuario;

import jakarta.validation.constraints.NotBlank;


public class LoginRequest {
    private String username; // nomeUsuario
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}