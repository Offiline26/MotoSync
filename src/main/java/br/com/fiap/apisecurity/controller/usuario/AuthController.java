package br.com.fiap.apisecurity.controller.usuario;

import br.com.fiap.apisecurity.dto.usuario.LoginRequest;
import br.com.fiap.apisecurity.dto.usuario.LoginResponse;
import br.com.fiap.apisecurity.dto.usuario.RegisterRequest;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.service.PatioService;
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

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository,
                          UsuarioService usuarioService,
                          PatioService patioService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.patioService = patioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> apiLogin(
            @RequestBody LoginRequest req,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            // salva a autenticação na SESSÃO (Opção A)
            var context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            org.springframework.security.core.context.SecurityContextHolder.setContext(context);
            new org.springframework.security.web.context.HttpSessionSecurityContextRepository()
                    .saveContext(context, request, response);

            Usuario u = usuarioRepository.findByEmail(req.getEmail()).orElseThrow();
            return ResponseEntity.ok(new LoginResponse(u.getId().toString(), u.getEmail(), u.getCargo()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos");
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

