package br.com.fiap.apisecurity.repository;
import br.com.fiap.apisecurity.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Buscar por e-mail (útil para login/autenticação)
    Optional<Usuario> findByEmail(String email);

    // Buscar por CPF (verificação de cadastro, por exemplo)
    Optional<Usuario> findByCpf(String cpf);

    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}

