package br.com.fiap.apisecurity.model;

import br.com.fiap.apisecurity.model.enums.StatusVaga;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_vaga")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double coordenadaLat;
    private Double coordenadaLong;

    @Enumerated(EnumType.STRING)
    private StatusVaga status;

    @ManyToOne
    private Patio patio;

    @OneToOne
    @JoinColumn(name = "moto_id")
    private Moto moto;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getCoordenadaLat() {
        return coordenadaLat;
    }

    public void setCoordenadaLat(Double coordenadaLat) {
        this.coordenadaLat = coordenadaLat;
    }

    public Double getCoordenadaLong() {
        return coordenadaLong;
    }

    public void setCoordenadaLong(Double coordenadaLong) {
        this.coordenadaLong = coordenadaLong;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga status) {
        this.status = status;
    }

    public Patio getPatio() {
        return patio;
    }

    public void setPatio(Patio patio) {
        this.patio = patio;
    }

    public Moto getMoto() {
        return moto;
    }

    public void setMoto(Moto moto) {
        this.moto = moto;
    }
}
