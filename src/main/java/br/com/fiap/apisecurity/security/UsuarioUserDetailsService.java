package br.com.fiap.apisecurity.security;

import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;

@Service
public class UsuarioUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public UsuarioUserDetailsService(UsuarioRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = repo.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("E-mail não encontrado"));
        var auth = List.of(new SimpleGrantedAuthority("ROLE_" + u.getCargo().name()));
        return new org.springframework.security.core.userdetails.User(u.getEmail(), u.getSenha(), auth);
    }
}
