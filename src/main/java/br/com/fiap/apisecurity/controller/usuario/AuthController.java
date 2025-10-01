package br.com.fiap.apisecurity.controller.usuario;

import br.com.fiap.apisecurity.dto.usuario.LoginRequest;
import br.com.fiap.apisecurity.dto.usuario.LoginResponse;
import br.com.fiap.apisecurity.dto.usuario.RegisterRequest;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.service.PatioService;
import br.com.fiap.apisecurity.service.usuario.JwtService;
import br.com.fiap.apisecurity.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService; // se usar
    private final PatioService patioService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          UsuarioService usuarioService,
                          PatioService patioService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.patioService = patioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(
            @RequestBody LoginRequest req,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response) {

        try {
            final String email = req.getEmail().trim().toLowerCase(java.util.Locale.ROOT);

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.getPassword())
            );

            // Gera JWT (sem salvar em sessão)
            org.springframework.security.core.userdetails.UserDetails principal =
                    (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();

            String token = jwtService.generate(principal.getUsername(), principal.getAuthorities());

            Usuario u = usuarioRepository.findByEmail(email).orElseThrow();

            // Corpo de resposta com token e dados do usuário
            java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
            body.put("accessToken", token);
            body.put("tokenType", "Bearer");
            body.put("user", new LoginResponse(u.getId().toString(), u.getEmail(), u.getCargo()));

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(body);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "E-mail ou senha inválidos"));
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterRequest());
        model.addAttribute("patios", patioService.findAllEntities()); // <- AQUI
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("form") RegisterRequest form,
                                 BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("patios", patioService.findAllEntities()); // <- AQUI também
            return "register";
        }
        try {
            usuarioService.register(form);
        } catch (IllegalArgumentException ex) {
            br.rejectValue("patioId", "invalid", ex.getMessage());
            model.addAttribute("patios", patioService.findAllEntities()); // <- AQUI também
            return "register";
        }
        ra.addFlashAttribute("ok", "Cadastro realizado com sucesso. Faça login.");
        return "redirect:/login";
    }

}

