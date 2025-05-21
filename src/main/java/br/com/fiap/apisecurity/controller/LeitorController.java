package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.LeitorDTO;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.enums.TipoLeitor;
import br.com.fiap.apisecurity.model.Vaga;
import br.com.fiap.apisecurity.service.LeitorService;
import br.com.fiap.apisecurity.service.PatioService;
import br.com.fiap.apisecurity.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/leitores")
public class LeitorController {

    private final LeitorService leitorService;
    private final PatioService patioService;
    private final VagaService vagaService;

    @Autowired
    public LeitorController(LeitorService leitorService, PatioService patioService, VagaService vagaService) {
        this.leitorService = leitorService;
        this.patioService = patioService;
        this.vagaService = vagaService;
    }

    @GetMapping
    public ResponseEntity<Page<LeitorDTO>> getAllLeitores(Pageable pageable) {
        return ResponseEntity.ok(leitorService.readAllLeitores(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeitorDTO> getLeitorById(@PathVariable UUID id) {
        LeitorDTO leitorDTO = leitorService.readLeitorById(id);
        if (leitorDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(leitorDTO);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<LeitorDTO>> getLeitoresByTipo(@PathVariable TipoLeitor tipo) {
        return ResponseEntity.ok(leitorService.readByTipo(tipo));
    }

    @GetMapping("/patio/{patioId}")
    public ResponseEntity<List<LeitorDTO>> getLeitoresByPatio(@PathVariable UUID patioId) {
        // Retorna a entidade Patio diretamente
        Patio patio = patioService.readPatioById(patioId);
        if (patio == null) return ResponseEntity.notFound().build();

        // Passa a entidade para o serviço e converte para DTO
        List<LeitorDTO> leitoresDTO = leitorService.readByPatio(patio);
        return ResponseEntity.ok(leitoresDTO);
    }

    @GetMapping("/vaga/{vagaId}/tipo/{tipo}")
    public ResponseEntity<LeitorDTO> getLeitorByVagaAndTipo(@PathVariable UUID vagaId, @PathVariable TipoLeitor tipo) {
        // Chama o serviço para pegar a entidade Vaga, não o DTO
        Vaga vaga = vagaService.readVagaById(vagaId);  // Agora estamos usando a entidade Vaga
        if (vaga == null) return ResponseEntity.notFound().build();  // Verifica se a vaga foi encontrada

        // Passa a entidade Vaga para o serviço de Leitor
        Optional<LeitorDTO> leitorOpt = leitorService.readByVagaAndTipo(vaga, tipo);
        return leitorOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<LeitorDTO> createLeitor(@RequestBody LeitorDTO leitorDTO) {
        return ResponseEntity.ok(leitorService.createLeitor(leitorDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeitorDTO> updateLeitor(@PathVariable UUID id, @RequestBody LeitorDTO leitorDTO) {
        LeitorDTO atualizado = leitorService.updateLeitor(id, leitorDTO);
        if (atualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeitor(@PathVariable UUID id) {
        leitorService.deleteLeitor(id);
        return ResponseEntity.noContent().build();
    }
}


