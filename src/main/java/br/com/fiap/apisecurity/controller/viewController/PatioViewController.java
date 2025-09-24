package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.service.PatioService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/patios")
public class PatioViewController {


    private final PatioService patioService;

    public PatioViewController(PatioService patioService) {
        this.patioService = patioService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "nome") Pageable pageable, Model model) {
        model.addAttribute("page", patioService.readAllPatios(pageable));
        return "patio/list";
    }


    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novo(Model model) {
        model.addAttribute("form", new PatioDTO());
        return "patio/form";
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String criar(PatioDTO form) {
        patioService.createPatio(form);
        return "redirect:/patios";
    }


    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editar(@PathVariable UUID id, Model model) {
        model.addAttribute("form", patioService.readPatioById(id));
        return "patio/form";
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizarOuExcluir(@PathVariable UUID id,
                                     @RequestParam(name = "_method", required = false) String method,
                                     PatioDTO form) {
        if ("delete".equalsIgnoreCase(method)) {
            patioService.deletePatio(id);
        } else if ("put".equalsIgnoreCase(method)) {
            patioService.updatePatio(id, form);
        }
        return "redirect:/patios";
    }
}
