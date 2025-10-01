package br.com.fiap.apisecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Application {

//Classe em que a aplicação funciona.
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
