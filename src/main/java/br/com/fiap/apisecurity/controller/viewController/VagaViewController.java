package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.dto.VagaDTO;
import br.com.fiap.apisecurity.service.PatioService;
import br.com.fiap.apisecurity.service.VagaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String list(@PageableDefault(size = 10, sort = "nome") Pageable pageable, Model model) {
        Page<VagaDTO> page = vagaService.readAllVagas(pageable);
        model.addAttribute("page", page);
        return "vaga/list";
    }


    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novo(Model model) {
        model.addAttribute("form", new VagaDTO());
        model.addAttribute("patios", patioService.readAllPatios(Pageable.unpaged()).getContent());
        return "vaga/form";
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String criar(VagaDTO form) {
        vagaService.createVaga(form);
        return "redirect:/vagas";
    }


    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editar(@PathVariable UUID id, Model model) {
        model.addAttribute("form", br.com.fiap.apisecurity.mapper.VagaMapper.toDto(vagaService.readVagaById(id)));
        model.addAttribute("patios", patioService.readAllPatios(Pageable.unpaged()).getContent());
        return "vaga/form";
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizarOuExcluir(@PathVariable UUID id,
                                     @RequestParam(name = "_method", required = false) String method,
                                     VagaDTO form) {
        if ("delete".equalsIgnoreCase(method)) {
            vagaService.deleteVaga(id);
        } else if ("put".equalsIgnoreCase(method)) {
            vagaService.updateVaga(id, form);
        }
        return "redirect:/vagas";
    }
}
