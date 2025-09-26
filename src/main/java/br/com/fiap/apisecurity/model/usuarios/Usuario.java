package br.com.fiap.apisecurity.model.usuarios;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    private String nomeUsuario;

    private String email;

    private String senha;

    private CargoUsuario cargo;

    public CargoUsuario getCargo() {
        return cargo;
    }

    public void setCargo(CargoUsuario cargo) {
        this.cargo = cargo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

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