package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.model.Patio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import br.com.fiap.apisecurity.model.enums.TipoLeitor;
import br.com.fiap.apisecurity.service.LeitorService;
import br.com.fiap.apisecurity.service.PatioService;

import java.util.UUID;

@Controller
@RequestMapping("/leitores")

public class LeitorViewController {


    private final LeitorService leitorService;
    private final PatioService patioService;

    public LeitorViewController(LeitorService leitorService, PatioService patioService) {
        this.leitorService = leitorService;
        this.patioService = patioService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("page", leitorService.readAllLeitores(pageable));
        return "leitor/list";
    }


    @GetMapping("/tipo/{tipo}")
    public String porTipo(@PathVariable TipoLeitor tipo, Model model) {
        model.addAttribute("leitores", leitorService.readByTipo(tipo));
        return "leitor/list"; // pode usar outra view específica
    }


    @GetMapping("/patio/{patioId}")
    public String porPatio(@PathVariable UUID patioId, Model model) {
        Patio patio = patioService.readPatioEntityById(patioId);
        model.addAttribute("leitores", leitorService.readByPatio(patio));
        return "leitor/list";
    }
}
