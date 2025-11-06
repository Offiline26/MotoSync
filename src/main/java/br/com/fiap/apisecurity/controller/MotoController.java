package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.mapper.MotoMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.service.Kafka.KafkaProducerService;
import br.com.fiap.apisecurity.service.MotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/motos")
public class MotoController {

    private final MotoService motoService;

    private KafkaProducerService kafkaProducerService;


    @Autowired
    public MotoController(MotoService motoService, KafkaProducerService kafkaProducerService) {
        this.motoService = motoService;
        this.kafkaProducerService = kafkaProducerService;
    }



    @GetMapping
    public ResponseEntity<Page<MotoDTO>> getAllMotos(Pageable pageable) {
        return ResponseEntity.ok(motoService.readAllMotos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoDTO> getMotoById(@PathVariable UUID id) {
        MotoDTO motoDTO = motoService.readMotoById(id);
        if (motoDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(motoDTO);
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<MotoDTO> getByPlaca(@PathVariable String placa) {
        Moto moto = motoService.readByPlaca(placa);
        if (moto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(MotoMapper.toDto(moto));
    }


    // ENTRADA / CRIAÇÃO
    @PostMapping
    public ResponseEntity<MotoDTO> createMoto(@RequestBody MotoDTO motoDTO) {
        MotoDTO criada = motoService.createMoto(motoDTO);

        // Se estiver associada a uma vaga, dispara evento
        if (criada.getVagaId() != null) {
            kafkaProducerService.sendVagaOcupadaEvent(
                    criada.getVagaId(),
                    criada.getId()
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // ATUALIZAÇÃO (pode ser mudança de vaga)
    @PutMapping("/{id}")
    public ResponseEntity<MotoDTO> updateMoto(@PathVariable UUID id,
                                              @RequestBody MotoDTO motoDTO) {

        // (Opcional) pegar estado anterior pra tratar saída/entrada de vagas diferentes
        MotoDTO antes = motoService.readMotoById(id);

        MotoDTO atualizada = motoService.updateMoto(id, motoDTO);
        if (atualizada == null) return ResponseEntity.notFound().build();

        // Se a vaga mudou OU continua na mesma, de qualquer forma a ocupação mudou → dispara
        if (atualizada.getVagaId() != null) {
            kafkaProducerService.sendVagaOcupadaEvent(
                    atualizada.getVagaId(),
                    atualizada.getId()
            );
        }

        // Se quiser ser mais chato, pode comparar antes.getVagaId() e atualizada.getVagaId()
        // e mandar evento para as duas vagas (saída e entrada)

        return ResponseEntity.ok(atualizada);
    }

    // SAÍDA (inativar / remover)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativarMoto(@PathVariable UUID id) {

        MotoDTO antes = motoService.readMotoById(id); // pra saber de qual vaga ela saiu
        motoService.inativarMoto(id);

        if (antes != null && antes.getVagaId() != null) {
            kafkaProducerService.sendVagaOcupadaEvent(
                    antes.getVagaId(),
                    id
            );
        }

        return ResponseEntity.noContent().build();
    }
}


