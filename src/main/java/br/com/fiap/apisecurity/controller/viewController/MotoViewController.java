package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.dto.MotoDTO;
import br.com.fiap.apisecurity.mapper.MotoMapper;
import br.com.fiap.apisecurity.model.enums.StatusMoto;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import br.com.fiap.apisecurity.service.MotoService;
import br.com.fiap.apisecurity.service.PatioService;
import br.com.fiap.apisecurity.service.VagaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Comparator;
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
                       @RequestParam(defaultValue = "false") boolean mostrarInativas,
                       @PageableDefault(size = 10, sort = "placa") Pageable pageable,
                       Model model) {

        if (placa != null && !placa.isBlank()) {
            var dto = motoService.readByPlacaDto(placa);
            var content = new ArrayList<MotoDTO>();
            if (dto != null) content.add(dto);
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
        var dto = new MotoDTO();
        dto.setStatus(br.com.fiap.apisecurity.model.enums.StatusMoto.DISPONIVEL);
        model.addAttribute("form", dto);

        var vagas = vagaService.readAllVagas(Pageable.unpaged()).getContent()
                .stream().filter(v -> v.getMoto() == null).toList();
        model.addAttribute("vagas", vagas);

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
    public String editar(@PathVariable UUID id, Model model, RedirectAttributes ra) {

        var form = motoService.readMotoById(id);
        if (form == null) { ra.addFlashAttribute("error","Moto não encontrada"); return "redirect:/motos"; }

        var todas = vagaService.readAllVagas(Pageable.unpaged()).getContent();
        var vagaAtualId = form.getVagaId();
        var vagas = todas.stream()
                .filter(v -> v.getMoto() == null || java.util.Objects.equals(v.getId(), vagaAtualId))
                .sorted(java.util.Comparator.comparing(v -> java.util.Optional.ofNullable(v.getIdentificacao()).orElse(v.getId().toString())))
                .toList();

        model.addAttribute("form", form);
        model.addAttribute("vagas", vagas);
        return "moto/form";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizar(@PathVariable UUID id,
                            @ModelAttribute("form") MotoDTO form,
                            RedirectAttributes ra) {
        try {
            motoService.updateMoto(id, form);
            ra.addFlashAttribute("ok", "Moto atualizada.");
            return "redirect:/motos";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/motos/" + id + "/editar";
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RedirectView excluir(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            motoService.inativarMoto(id); // libera vaga + INATIVADA
            ra.addFlashAttribute("ok", "Moto inativada.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        RedirectView rv = new RedirectView("/motos", true);
        rv.setStatusCode(HttpStatus.SEE_OTHER); // 303 PRG “duro”
        return rv;
    }
}
