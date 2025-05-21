package br.com.fiap.apisecurity.controller;

import br.com.fiap.apisecurity.dto.LoginDTO;
import br.com.fiap.apisecurity.dto.TokenDTO;
import br.com.fiap.apisecurity.model.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken credentials =
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha());

        Authentication authentication = authenticationManager.authenticate(credentials);

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = tokenService.generateToken(usuario);

        return ResponseEntity.ok(new TokenDTO(token));
    }
}