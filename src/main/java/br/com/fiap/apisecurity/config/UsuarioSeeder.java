package br.com.fiap.apisecurity.config;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import br.com.fiap.apisecurity.model.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class UsuarioSeeder {

    @Bean
    CommandLineRunner initUsuario(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        return args -> {
            if (usuarioRepository.findByEmail("thiago@email.com").isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setEmail("thiago@email.com");
                usuario.setSenha(encoder.encode("123456"));
                usuario.setCargo(CargoUsuario.ADMIN);

                usuarioRepository.save(usuario);
                System.out.println("🟢 Usuário de teste criado com sucesso!");
            }
        };
    }
}
