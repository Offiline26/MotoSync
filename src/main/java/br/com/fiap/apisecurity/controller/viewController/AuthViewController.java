package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.controller.usuario.Authz;
import br.com.fiap.apisecurity.dto.usuario.RegisterRequest;
import br.com.fiap.apisecurity.service.PatioService;
import br.com.fiap.apisecurity.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthViewController {

    private final UsuarioService usuarioService;
    private final PatioService patioService;
    private final Authz authz;

    public AuthViewController(UsuarioService usuarioService, PatioService patioService, Authz authz) {
        this.usuarioService = usuarioService;
        this.patioService = patioService;
        this.authz = authz;
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("patios", patioService.findAllEntities());

        boolean isAdmin = false;
        try { isAdmin = authz.isAdmin(); } catch (SecurityException ignored) { }
        model.addAttribute("isAdmin", isAdmin);

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterRequest());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("form") RegisterRequest form,
                                 BindingResult br,
                                 Model model,
                                 RedirectAttributes ra) {

        // Validação condicional do pátio (funciona se cargo for enum ou String)
        final String cargo = form.getCargo() != null ? form.getCargo().toString() : null;
        final boolean isOperadorPatio = "OPERADOR_PATIO".equalsIgnoreCase(cargo);

        if (isOperadorPatio && form.getPatioId() == null) {
            br.rejectValue("patioId", "patio.required", "Selecione o pátio do operador.");
        }

        // Para ADMIN (ou qualquer outro cargo), não manter patioId “fantasma”
        if (!isOperadorPatio) {
            form.setPatioId(null);
        }

        if (br.hasErrors()) {
            model.addAttribute("patios", patioService.findAllEntities());
            return "register";
        }

        try {
            usuarioService.register(form);
            ra.addFlashAttribute("ok", "Cadastro realizado com sucesso. Faça login.");
            return "redirect:/login?registered";
        } catch (DataIntegrityViolationException ex) {
            // e-mail duplicado, etc.
            model.addAttribute("error", ex.getMostSpecificCause() != null
                    ? ex.getMostSpecificCause().getMessage()
                    : ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // mensagens de domínio vindas do service (ex.: pátio inválido)
            br.rejectValue("patioId", "invalid", ex.getMessage());
        }

        model.addAttribute("patios", patioService.findAllEntities());
        return "register";
    }
}
