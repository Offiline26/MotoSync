package br.com.fiap.apisecurity.controller.viewController;

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

    public AuthViewController(UsuarioService usuarioService, PatioService patioService) {
        this.usuarioService = usuarioService;
        this.patioService = patioService;
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterRequest());
        model.addAttribute("patios", patioService.findAllEntities()); // lista para o <select>
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("form") RegisterRequest form,
                                 BindingResult br,
                                 Model model,
                                 RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("patios", patioService.findAllEntities());
            return "register";
        }
        try {
            usuarioService.register(form);
        } catch (IllegalArgumentException ex) {
            br.rejectValue("patioId", "invalid", ex.getMessage());
            model.addAttribute("patios", patioService.findAllEntities());
            return "register";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("patios", patioService.findAllEntities());
            return "register";
        }
        ra.addFlashAttribute("ok", "Cadastro realizado com sucesso. Faça login.");
        return "redirect:/login?registered";
    }
}
