package br.com.fiap.apisecurity.controller.usuario;

import br.com.fiap.apisecurity.dto.usuario.UpdateUsuarioDTO;
import br.com.fiap.apisecurity.dto.usuario.UsuarioDTO;
import br.com.fiap.apisecurity.dto.usuario.UsuarioPerfilResponse;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioDTO dto) {
        try {
            Usuario novoUsuario = usuarioService.salvar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarPerfil(
            @RequestBody UpdateUsuarioDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<Usuario> optional = usuarioRepository.findByNomeUsuario(userDetails.getUsername());
        if (optional.isEmpty()) return ResponseEntity.status(404).body("Usuário não encontrado");

        Usuario usuario = optional.get();

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(401).body("Senha incorreta");
        }

        usuario.setNome(dto.getNome());
        usuario.setNomeUsuario(dto.getNomeUsuario());
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Perfil atualizado com sucesso");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPerfilResponse> buscarPerfil(@PathVariable UUID id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(usuarioService.montarPerfilParaFrontend(usuarioOpt.get()));
    }

    @GetMapping("/debug/{id}")
    public ResponseEntity<?> debug(@PathVariable UUID id) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        System.out.println(u.getPostagens().size()); // ⚠️ deve explodir se estiver fora da transação
        return ResponseEntity.ok("ok");
    }

}


