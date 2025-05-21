package br.com.fiap.apisecurity.model;

import br.com.fiap.apisecurity.model.enums.StatusMoto;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_moto")
public class Moto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 7, nullable = false, unique = true)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMoto status;

    @Column(name = "vaga_id")
    private UUID vagaId;

    @OneToOne(mappedBy = "moto", cascade = CascadeType.ALL)
    private Sensor sensor;

    public Moto() {}

    public Moto(String placa, StatusMoto status) {
        this.placa = placa;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public StatusMoto getStatus() {
        return status;
    }

    public void setStatus(StatusMoto status) {
        this.status = status;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public void setVagaId(UUID vagaId) {
        this.vagaId = vagaId;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
