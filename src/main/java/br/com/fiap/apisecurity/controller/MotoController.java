package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.mapper.MotoMapper;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.service.MotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/motos")
public class MotoController {

    private final MotoService motoService;

    @Autowired
    public MotoController(MotoService motoService) {
        this.motoService = motoService;
    }

    @GetMapping
    public ResponseEntity<Page<MotoDTO>> getAllMotos(Pageable pageable) {
        return ResponseEntity.ok(motoService.readAllMotos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoDTO> getMotoById(@PathVariable UUID id) {
        // Obtém a entidade Moto
        Moto moto = motoService.readMotoById(id);
        if (moto == null) return ResponseEntity.notFound().build();

        // Converte a entidade Moto para MotoDTO
        MotoDTO motoDTO = MotoMapper.toDto(moto);

        return ResponseEntity.ok(motoDTO);  // Retorna o DTO
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<MotoDTO> getByPlaca(@PathVariable String placa) {
        Moto moto = motoService.readByPlaca(placa);
        if (moto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(MotoMapper.toDto(moto));
    }

    @PostMapping
    public ResponseEntity<MotoDTO> createMoto(@RequestBody MotoDTO motoDTO) {
        return ResponseEntity.ok(motoService.createMoto(motoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotoDTO> updateMoto(@PathVariable UUID id, @RequestBody MotoDTO motoDTO) {
        MotoDTO updated = motoService.updateMoto(id, motoDTO);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoto(@PathVariable UUID id) {
        motoService.deleteMoto(id);
        return ResponseEntity.noContent().build();
    }
}


