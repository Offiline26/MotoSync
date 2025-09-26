package br.com.fiap.apisecurity.dto.usuario;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "O nome de usuário é obrigatório.")
    @Size(min = 3, max = 25, message = "O nome de usuário deve ter entre 3 e 25 caracteres.")
    private String nomeUsuario;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ser válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
    private String senha;

    @NotNull(message = "O cargo do usuário é obrigatório.")
    private CargoUsuario cargo;

    // getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public CargoUsuario getCargo() { return cargo; }
    public void setCargo(CargoUsuario cargo) { this.cargo = cargo; }
}

