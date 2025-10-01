package br.com.fiap.apisecurity.controller.viewController;

import br.com.fiap.apisecurity.service.RegistroService;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/registros")
public class RegistroViewController {


    private final RegistroService registroService;

    public RegistroViewController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @GetMapping
    public String list(@PageableDefault(size = 10) Pageable pageable, Model model) {
        model.addAttribute("page", registroService.readAllRegistros(pageable));
        return "registro/list";
    }


    @GetMapping("/periodo")
    public String porPeriodo(@RequestParam("inicio") String inicio,
                             @RequestParam("fim") String fim,
                             Model model) {
        var i = LocalDateTime.parse(inicio);
        var f = LocalDateTime.parse(fim);
        model.addAttribute("registros", registroService.readByPeriodo(i, f));
        return "registro/list";
    }
}
