package br.com.fiap.apisecurity.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_sensor")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codigoUnico;

    @Enumerated(EnumType.STRING)
    private StatusSensor status;

    @OneToOne
    @JoinColumn(name = "moto_id", nullable = false, unique = true)
    private Moto moto;

    public Sensor() {}

    public Sensor(String codigoUnico, StatusSensor status, Moto moto) {
        this.codigoUnico = codigoUnico;
        this.status = status;
        this.moto = moto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public StatusSensor getStatus() {
        return status;
    }

    public void setStatus(StatusSensor status) {
        this.status = status;
    }

    public Moto getMoto() {
        return moto;
    }

    public void setMoto(Moto moto) {
        this.moto = moto;
    }
}
