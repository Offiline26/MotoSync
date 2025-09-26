package br.com.fiap.apisecurity.repository;

import br.com.fiap.apisecurity.model.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByNomeUsuario(String nomeUsuario);

    Optional<Usuario> findByEmail(String email);
}
