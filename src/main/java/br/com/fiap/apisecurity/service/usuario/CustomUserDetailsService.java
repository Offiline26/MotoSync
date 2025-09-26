package br.com.fiap.apisecurity.service.usuario;

import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNomeUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return new org.springframework.security.core.userdetails.User(
                usuario.getNomeUsuario(),
                usuario.getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
