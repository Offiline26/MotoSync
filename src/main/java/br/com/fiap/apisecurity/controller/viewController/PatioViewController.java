package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.service.PatioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@Controller
@RequestMapping("/patios")
public class PatioViewController {


    private final PatioService patioService;

    public PatioViewController(PatioService patioService) {
        this.patioService = patioService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "nome") Pageable pageable,
                       Model model,
                       HttpServletResponse resp) {

        // Mesmo no-cache do controller de Motos
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        resp.addHeader("Cache-Control", "post-check=0, pre-check=0"); // legacy compat
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        var page = patioService.readAllPatios(pageable);
        model.addAttribute("page", page);
        model.addAttribute("content", page.getContent());
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
    public String criar(@ModelAttribute("form") PatioDTO form) {
        patioService.createPatio(form);
        return "redirect:/patios";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editar(@PathVariable UUID id, Model model, RedirectAttributes ra) {
        var form = patioService.readPatioById(id);
        if (form == null) {
            ra.addFlashAttribute("error", "Pátio não encontrado");
            return "redirect:/patios";
        }
        model.addAttribute("form", form);
        return "patio/form";
    }

    // PUT real — o HiddenHttpMethodFilter converterá o POST + _method=put do form
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizar(@PathVariable UUID id,
                            @ModelAttribute("form") PatioDTO form,
                            RedirectAttributes ra) {
        try {
            patioService.updatePatio(id, form);
            ra.addFlashAttribute("ok", "Pátio atualizado.");
            return "redirect:/patios";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/patios/" + id + "/editar";
        }
    }

    // DELETE real — idem (POST + _method=delete nos templates)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RedirectView excluir(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            patioService.inativarPatio(id);
            ra.addFlashAttribute("ok", "Pátio inativado.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage()); // << importante pro alerta
        }
        var rv = new RedirectView("/patios", true);
        rv.setStatusCode(HttpStatus.SEE_OTHER);
        return rv;
    }
}
