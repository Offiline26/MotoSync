package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.mapper.VagaMapper;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.service.PatioService;
import br.com.fiap.apisecurity.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vagas")
public class VagaController {

    private final VagaService vagaService;
    private final PatioService patioService;

    @Autowired
    public VagaController(VagaService vagaService, PatioService patioService) {
        this.vagaService = vagaService;
        this.patioService = patioService;
    }

    @GetMapping
    public ResponseEntity<Page<VagaDTO>> getAllVagas(Pageable pageable) {
        return ResponseEntity.ok(vagaService.readAllVagas(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VagaDTO> getVagaById(@PathVariable UUID id) {
        // Recupera a entidade Vaga
        Vaga vaga = vagaService.readVagaById(id);
        if (vaga == null) return ResponseEntity.notFound().build();

        // Converte a entidade Vaga para VagaDTO
        VagaDTO vagaDTO = VagaMapper.toDto(vaga);

        // Retorna o DTO
        return ResponseEntity.ok(vagaDTO);
    }

    @GetMapping("/patio/{patioId}/status/{status}")
    public ResponseEntity<List<VagaDTO>> getVagasByPatioAndStatus(@PathVariable UUID patioId, @PathVariable StatusVaga status) {
        List<VagaDTO> vagasDTO = vagaService.readByPatioAndStatus(patioId, status);
        if (vagasDTO.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(vagasDTO);
    }

    @PostMapping
    public ResponseEntity<VagaDTO> createVaga(@RequestBody VagaDTO vagaDTO) {
        return ResponseEntity.ok(vagaService.createVaga(vagaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VagaDTO> updateVaga(@PathVariable UUID id, @RequestBody VagaDTO vagaDTO) {
        VagaDTO atualizado = vagaService.updateVaga(id, vagaDTO);
        if (atualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVaga(@PathVariable UUID id) {
        vagaService.deleteVaga(id);
        return ResponseEntity.noContent().build();
    }
}

