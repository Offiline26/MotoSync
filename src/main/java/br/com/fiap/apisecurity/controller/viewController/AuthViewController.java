package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.dto.usuario.RegisterRequest;
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

    public AuthViewController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("form") RegisterRequest form,
                                 BindingResult br,
                                 RedirectAttributes ra) {
        if (br.hasErrors()) return "register";
        try {
            usuarioService.register(form);
            ra.addFlashAttribute("ok", "Conta criada com sucesso. Faça login.");
            return "redirect:/login?registered";
        } catch (DataIntegrityViolationException e) {
            br.rejectValue("email", "exists", "E-mail já cadastrado");
            return "register";
        }
    }
}
