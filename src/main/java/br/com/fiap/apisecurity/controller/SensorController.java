package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.SensorDTO;
import br.com.fiap.apisecurity.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sensores")
public class SensorController {

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping
    public ResponseEntity<Page<SensorDTO>> getAllSensores(Pageable pageable) {
        return ResponseEntity.ok(sensorService.readAllSensores(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable UUID id) {
        SensorDTO sensorDTO = sensorService.readSensorById(id);
        if (sensorDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(sensorDTO);
    }

    @GetMapping("/codigo/{codigoUnico}")
    public ResponseEntity<SensorDTO> getByCodigoUnico(@PathVariable String codigoUnico) {
        SensorDTO sensorDTO = sensorService.readByCodigoUnico(codigoUnico);
        if (sensorDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(sensorDTO);
    }

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@RequestBody SensorDTO sensorDTO) {
        return ResponseEntity.ok(sensorService.createSensor(sensorDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable UUID id, @RequestBody SensorDTO sensorDTO) {
        SensorDTO updated = sensorService.updateSensor(id, sensorDTO);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable UUID id) {
        sensorService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}


