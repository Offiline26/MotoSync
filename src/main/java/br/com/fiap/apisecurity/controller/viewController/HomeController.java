package br.com.fiap.apisecurity.controller.viewController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"/", "/index"})
    public String home() {
        return "index"; // resolve templates/index.html
    }
}
