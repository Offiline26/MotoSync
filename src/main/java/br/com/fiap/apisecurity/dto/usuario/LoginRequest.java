package br.com.fiap.apisecurity.dto.usuario;

import jakarta.validation.constraints.NotBlank;


public class LoginRequest {
    private String email; // nomeUsuario
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}