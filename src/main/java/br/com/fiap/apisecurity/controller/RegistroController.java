package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.RegistroDTO;
import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.TipoMovimentacao;
import br.com.fiap.apisecurity.service.MotoService;
import br.com.fiap.apisecurity.service.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/registros")
public class RegistroController {

    private final RegistroService registroService;
    private final MotoService motoService;

    @Autowired
    public RegistroController(RegistroService registroService, MotoService motoService) {
        this.registroService = registroService;
        this.motoService = motoService;
    }

    @GetMapping
    public ResponseEntity<Page<RegistroDTO>> getAllRegistros(Pageable pageable) {
        return ResponseEntity.ok(registroService.readAllRegistros(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroDTO> getRegistroById(@PathVariable UUID id) {
        RegistroDTO registroDTO = registroService.readRegistroById(id);
        if (registroDTO == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(registroDTO);
    }

    @GetMapping("/moto/{motoId}")
    public ResponseEntity<List<RegistroDTO>> getByMoto(@PathVariable UUID motoId) {
        // Passa a entidade Moto para o serviço de Registro
        Moto moto = motoService.readMotoById(motoId);  // Agora estamos usando a entidade Moto
        if (moto == null) return ResponseEntity.notFound().build();  // Verifica se a moto foi encontrada

        return ResponseEntity.ok(registroService.readByMoto(moto));  // Passa a entidade Moto
    }

    @GetMapping("/moto/{motoId}/tipo/{tipo}")
    public ResponseEntity<List<RegistroDTO>> getByMotoAndTipo(@PathVariable UUID motoId, @PathVariable TipoMovimentacao tipo) {
        // Passa a entidade Moto para o serviço de Registro
        Moto moto = motoService.readMotoById(motoId);  // Agora estamos usando a entidade Moto
        if (moto == null) return ResponseEntity.notFound().build();  // Verifica se a moto foi encontrada

        return ResponseEntity.ok(registroService.readByMotoAndTipo(moto, tipo));  // Passa a entidade Moto
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<RegistroDTO>> getByPeriodo(
            @RequestParam("inicio") LocalDateTime inicio,
            @RequestParam("fim") LocalDateTime fim
    ) {
        return ResponseEntity.ok(registroService.readByPeriodo(inicio, fim));
    }

    @PostMapping
    public ResponseEntity<RegistroDTO> createRegistro(@RequestBody RegistroDTO registroDTO) {
        return ResponseEntity.ok(registroService.createRegistro(registroDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroDTO> updateRegistro(@PathVariable UUID id, @RequestBody RegistroDTO registroDTO) {
        RegistroDTO atualizado = registroService.updateRegistro(id, registroDTO);
        if (atualizado == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistro(@PathVariable UUID id) {
        registroService.deleteRegistro(id);
        return ResponseEntity.noContent().build();
    }
}

