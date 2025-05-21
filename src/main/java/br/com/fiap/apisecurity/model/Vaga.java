package br.com.fiap.apisecurity.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_vaga")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private double coordenadaLat;
    private double coordenadaLong;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVaga status;

    @ManyToOne
    @JoinColumn(name = "patio_id", nullable = false)
    private Patio patio;

    public Vaga() {}

    public Vaga(double coordenadaLat, double coordenadaLong, StatusVaga status, Patio patio) {
        this.coordenadaLat = coordenadaLat;
        this.coordenadaLong = coordenadaLong;
        this.status = status;
        this.patio = patio;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public double getCoordenadaLat() {
        return coordenadaLat;
    }

    public void setCoordenadaLat(double coordenadaLat) {
        this.coordenadaLat = coordenadaLat;
    }

    public double getCoordenadaLong() {
        return coordenadaLong;
    }

    public void setCoordenadaLong(double coordenadaLong) {
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
}
