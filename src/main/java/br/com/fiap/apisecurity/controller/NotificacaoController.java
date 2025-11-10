package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final UsuarioRepository usuarioRepository;

    public NotificacaoController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/expo-token")
    public ResponseEntity<Void> registrarExpoToken(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails principal
    ) {
        String expoToken = body.get("expoToken");
        if (expoToken == null || expoToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Usuario usuario = usuarioRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setExpoPushToken(expoToken); // <-- campo na entidade Usuario
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().build();
    }
}
