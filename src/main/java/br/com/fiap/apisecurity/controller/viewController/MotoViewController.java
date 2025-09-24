package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.mapper.MotoMapper;
import br.com.fiap.apisecurity.service.MotoService;
import br.com.fiap.apisecurity.service.PatioService;
import br.com.fiap.apisecurity.service.VagaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@Controller
@RequestMapping("/motos")
public class MotoViewController {


    private final MotoService motoService;
    private final VagaService vagaService;
    private final PatioService patioService;

    public MotoViewController(MotoService motoService, VagaService vagaService, PatioService patioService) {
        this.motoService = motoService;
        this.vagaService = vagaService;
        this.patioService = patioService;
    }


    @GetMapping
    public String list(@RequestParam(required = false) String placa,
                       @PageableDefault(size = 10, sort = "placa") Pageable pageable,
                       Model model) {

        if (placa != null && !placa.isBlank()) {
            var moto = motoService.readByPlaca(placa);
            var content = new ArrayList<MotoDTO>();
            if (moto != null) content.add(MotoMapper.toDto(moto));

            // força page=0 para páginas “sintéticas” de busca
            var p0 = PageRequest.of(0, pageable.getPageSize(), pageable.getSort());
            var page = new PageImpl<>(content, p0, content.size());

            model.addAttribute("page", page);
            model.addAttribute("content", page.getContent());
        } else {
            var page = motoService.readAllMotos(pageable);
            model.addAttribute("page", page);
            model.addAttribute("content", page.getContent());
        }
        return "moto/list";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novo(Model model) {
        model.addAttribute("form", new MotoDTO());

        // usa os services conforme expostos: retornam Page<DTO>
        model.addAttribute("patios", patioService.readAllPatios(Pageable.unpaged()).getContent());
        model.addAttribute("vagas",  vagaService.readAllVagas(Pageable.unpaged()).getContent());

        return "moto/form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String criar(@ModelAttribute("form") MotoDTO form) {
        motoService.createMoto(form);
        return "redirect:/motos";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editar(@PathVariable UUID id, Model model) {
        model.addAttribute("form", motoService.readMotoById(id));

        // novamente, seguindo o padrão do PatioService
        model.addAttribute("patios", patioService.readAllPatios(Pageable.unpaged()).getContent());
        model.addAttribute("vagas",  vagaService.readAllVagas(Pageable.unpaged()).getContent());

        return "moto/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizarOuExcluir(@PathVariable UUID id,
                                     @RequestParam(name = "_method", required = false) String method,
                                     @ModelAttribute("form") MotoDTO form) {
        if ("delete".equalsIgnoreCase(method)) {
            motoService.deleteMoto(id);
        } else if ("put".equalsIgnoreCase(method)) {
            motoService.updateMoto(id, form);
        }
        return "redirect:/motos";
    }
}
