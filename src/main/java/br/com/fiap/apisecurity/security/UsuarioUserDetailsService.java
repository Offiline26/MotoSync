package br.com.fiap.apisecurity.security;

import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByNomeUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        CargoUsuario cargo = u.getCargo(); // enum em Usuario
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + cargo.name()));

        return new org.springframework.security.core.userdetails.User(
                u.getNomeUsuario(), // username
                u.getSenha(),
                authorities
        );
    }
}