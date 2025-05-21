package br.com.fiap.apisecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Habilita o suporte a cache do Spring
public class Application {

//Classe em que a aplicação funciona.
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
