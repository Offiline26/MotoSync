package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.service.PatioService;
import br.com.fiap.apisecurity.service.VagaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/vagas")
public class VagaViewController {


    private final VagaService vagaService;
    private final PatioService patioService;

    public VagaViewController(VagaService vagaService, PatioService patioService) {
        this.vagaService = vagaService;
        this.patioService = patioService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "identificacao") Pageable pageable,
                       Model model,
                       HttpServletRequest req,
                       HttpServletResponse resp) {

        // anti-cache para a listagem (atualiza na hora após editar/excluir)
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        var page = vagaService.readAllVagas(pageable);
        model.addAttribute("page", page);

        // preserve o(s) parâmetros sort da URL (pode haver vários)
        String[] sortParams = req.getParameterValues("sort"); // ex.: ["identificacao,asc", "patio.nome,desc"]
        model.addAttribute("sortParams", sortParams); // pode ser null

        return "vaga/list";
    }


    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novo(Model model) {
        var dto = new VagaDTO();
        model.addAttribute("form", dto);

        // carrega pátios para o select, ordenados por nome (ou id se nome for null)
        var patios = patioService.readAllPatios(Pageable.unpaged()).getContent()
                .stream()
                .sorted(Comparator.comparing(p -> Optional.ofNullable(p.getNome())
                        .orElse(p.getId().toString())))
                .toList();
        model.addAttribute("patios", patios);

        model.asMap().remove("page");
        return "vaga/form";
    }



    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String criar(@ModelAttribute("form") VagaDTO form, RedirectAttributes ra) {
        vagaService.createVaga(form);
        ra.addFlashAttribute("ok", "Vaga criada.");
        return "redirect:/vagas";
    }


    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editar(@PathVariable UUID id, Model model) {
        model.addAttribute("form", br.com.fiap.apisecurity.mapper.VagaMapper.toDto(vagaService.readVagaById(id)));
        model.addAttribute("patios", patioService.readAllPatios(Pageable.unpaged()).getContent());
        model.asMap().remove("page");

        return "vaga/form";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public org.springframework.web.servlet.view.RedirectView atualizar(@PathVariable UUID id,
                                                                       @ModelAttribute VagaDTO form,
                                                                       RedirectAttributes ra) {
        // opcional: garantir consistência do id
        form.setId(id);
        vagaService.updateVaga(id, form);
        ra.addFlashAttribute("ok", "Vaga atualizada.");
        var rv = new org.springframework.web.servlet.view.RedirectView("/vagas", true);
        rv.setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER); // PRG duro
        return rv;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public org.springframework.web.servlet.view.RedirectView excluir(@PathVariable UUID id,
                                                                     RedirectAttributes ra) {
        vagaService.deleteVaga(id);
        ra.addFlashAttribute("ok", "Vaga excluída.");
        var rv = new org.springframework.web.servlet.view.RedirectView("/vagas", true);
        rv.setStatusCode(org.springframework.http.HttpStatus.SEE_OTHER); // PRG duro
        return rv;
    }
}
