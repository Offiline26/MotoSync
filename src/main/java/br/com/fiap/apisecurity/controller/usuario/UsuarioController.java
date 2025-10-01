package br.com.fiap.apisecurity.controller.usuario;

import br.com.fiap.apisecurity.dto.usuario.UpdateUsuarioDTO;
import br.com.fiap.apisecurity.dto.usuario.UsuarioPerfilResponse;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.service.usuario.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService,
                             UsuarioRepository usuarioRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilResponse> me(@AuthenticationPrincipal UserDetails principal) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(principal.getUsername());
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(usuarioService.montarPerfilParaFrontend(opt.get()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPerfilResponse> buscarPerfil(@PathVariable UUID id) {
        return usuarioRepository.findById(id)
                .map(usuarioService::montarPerfilParaFrontend)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<?> atualizarPerfil(@RequestBody UpdateUsuarioDTO dto,
                                             @AuthenticationPrincipal UserDetails principal) {

        Optional<Usuario> opt = usuarioRepository.findByEmail(principal.getUsername());
        if (opt.isEmpty()) return ResponseEntity.status(404).body("Usuário não encontrado");

        Usuario usuario = opt.get();

        if (dto.getSenhaAtual() == null || !passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            return ResponseEntity.status(401).body("Senha atual incorreta");
        }

        if (dto.getNovoEmail() != null && !dto.getNovoEmail().isBlank()) {
            String novoEmail = dto.getNovoEmail().trim().toLowerCase();
            // checar unicidade
            var existente = usuarioRepository.findByEmail(novoEmail);
            if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
                return ResponseEntity.badRequest().body("E-mail já está em uso");
            }
            usuario.setEmail(novoEmail);
        }

        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        }

        if (dto.getNovoCargo() != null) {
            usuario.setCargo(dto.getNovoCargo());
        }

        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Perfil atualizado com sucesso");
    }
}
