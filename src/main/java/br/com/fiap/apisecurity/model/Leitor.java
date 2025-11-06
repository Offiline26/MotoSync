package br.com.fiap.apisecurity.model;

import br.com.fiap.apisecurity.model.enums.TipoLeitor;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_leitor")
public class Leitor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLeitor tipo;

    @ManyToOne
    @JoinColumn(name = "patio_id", nullable = false)
    private Patio patio;

    @ManyToOne
    @JoinColumn(name = "vaga_id")
    private Vaga vaga;

    public Leitor() {}

    public Leitor(TipoLeitor tipo, Patio patio, Vaga vaga) {
        this.tipo = tipo;
        this.patio = patio;
        this.vaga = vaga;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TipoLeitor getTipo() {
        return tipo;
    }

    public void setTipo(TipoLeitor tipo) {
        this.tipo = tipo;
    }

    public Patio getPatio() {
        return patio;
    }

    public void setPatio(Patio patio) {
        this.patio = patio;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }
}

