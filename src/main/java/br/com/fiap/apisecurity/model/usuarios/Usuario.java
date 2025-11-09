package br.com.fiap.apisecurity.model.usuarios;

import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "id")
    private UUID id;

    @Email @NotBlank
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false, length = 50)
    private CargoUsuario cargo = CargoUsuario.OPERADOR_PATIO;

    @Column(name = "expo_push_token")
    private String expoPushToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patio_id")
    private Patio patio;

    public Patio getPatio() {
        return patio;
    }

    public void setPatio(Patio patio) {
        this.patio = patio;
    }

    public String getExpoPushToken() { return expoPushToken;}
    public void setExpoPushToken(String expoPushToken) { this.expoPushToken = expoPushToken;}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public CargoUsuario getCargo() { return cargo; }
    public void setCargo(CargoUsuario cargo) { this.cargo = cargo; }
}
