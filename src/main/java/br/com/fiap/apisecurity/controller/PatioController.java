package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.mapper.PatioMapper;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.service.PatioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patios")
public class PatioController {

    private final PatioService patioService;

    public PatioController(PatioService patioService) {
        this.patioService = patioService;
    }

    @GetMapping
    public Page<PatioDTO> list(@PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return patioService.readAllPatios(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatioDTO> getOne(@PathVariable UUID id) {
        var dto = patioService.readPatioById(id);
        return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PatioDTO> create(@Valid @RequestBody PatioDTO body) {
        var saved = patioService.createPatio(body);
        return ResponseEntity
                .created(URI.create("/api/patios/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatioDTO> update(@PathVariable UUID id, @Valid @RequestBody PatioDTO body) {
        var updated = patioService.updatePatio(id, body);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        patioService.inativarPatio(id);
        return ResponseEntity.noContent().build();
    }
}


