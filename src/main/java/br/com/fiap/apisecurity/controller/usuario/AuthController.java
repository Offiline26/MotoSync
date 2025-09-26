package br.com.fiap.apisecurity.controller.usuario;

import br.com.fiap.apisecurity.dto.usuario.LoginRequest;
import br.com.fiap.apisecurity.dto.usuario.LoginResponse;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.service.usuario.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), // = nomeUsuario
                            request.getPassword()
                    )
            );

            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(user);

            Usuario u = usuarioRepository.findByNomeUsuario(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            return ResponseEntity.ok(
                    new LoginResponse(token, u.getId(), u.getCargo())
            );

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos");
        }
    }
}
