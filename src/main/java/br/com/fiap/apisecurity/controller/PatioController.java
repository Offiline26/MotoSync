package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.mapper.PatioMapper;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.service.PatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patios")
public class PatioController {

    private final PatioService patioService;

    @Autowired
    public PatioController(PatioService patioService) {
        this.patioService = patioService;
    }

    @GetMapping
    public ResponseEntity<Page<PatioDTO>> getAllPatios(Pageable pageable) {
        return ResponseEntity.ok(patioService.readAllPatios(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatioDTO> getPatioById(@PathVariable UUID id) {
        // Chama o serviço para pegar a entidade Patio, não o DTO
        Patio patio = patioService.readPatioById(id);  // Agora estamos usando a entidade Patio
        if (patio == null) return ResponseEntity.notFound().build();  // Verifica se a entidade foi encontrada

        // Converte a entidade Patio para DTO antes de retornar a resposta
        PatioDTO patioDTO = PatioMapper.toDto(patio);  // Converte para DTO
        return ResponseEntity.ok(patioDTO);
    }


    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<List<PatioDTO>> getPatiosByCidade(@PathVariable String cidade) {
        return ResponseEntity.ok(patioService.readByCidade(cidade));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<PatioDTO> getPatioByNome(@PathVariable String nome) {
        PatioDTO dto = patioService.readByNome(nome);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PatioDTO> createPatio(@RequestBody PatioDTO patioDTO) {
        return ResponseEntity.ok(patioService.createPatio(patioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatioDTO> updatePatio(@PathVariable UUID id, @RequestBody PatioDTO patioDTO) {
        PatioDTO atualizado = patioService.updatePatio(id, patioDTO);
        if (atualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatio(@PathVariable UUID id) {
        patioService.deletePatio(id);
        return ResponseEntity.noContent().build();
    }
}


