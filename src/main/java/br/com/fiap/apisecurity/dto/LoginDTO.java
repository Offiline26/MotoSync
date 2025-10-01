package br.com.fiap.apisecurity.dto;

public class LoginDTO {
    @jakarta.validation.constraints.Email
    private String email;

    @jakarta.validation.constraints.NotBlank
    private String senha;

    // getters e setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

