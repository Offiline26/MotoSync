package br.com.fiap.apisecurity.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUsuarioDTO {
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Size(min = 3, max = 25, message = "O nome de usuário deve ter entre 3 e 25 caracteres.")
    private String nomeUsuario;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;

    // Getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
