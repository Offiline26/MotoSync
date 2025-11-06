package br.com.fiap.apisecurity.controller.usuario;

import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("authz")
public class Authz {


    private final UsuarioRepository usuarioRepository;

    public Authz(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario currentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new SecurityException("Usuário não autenticado.");
        }
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new SecurityException("Usuário autenticado não encontrado."));
    }

    public boolean isAdmin() {
        Usuario u = currentUserOrThrow();
        return u.getCargo() == CargoUsuario.ADMIN;
    }

    public UUID currentUserPatioIdOrNull() {
        Usuario u = currentUserOrThrow();
        Patio p = u.getPatio();
        return (p != null) ? p.getId() : null;
    }

    public UUID currentUserPatioIdOrThrow() {
        UUID patioId = currentUserPatioIdOrNull();
        if (patioId == null) {
            throw new SecurityException("Operador sem pátio vinculado.");
        }
        return patioId;
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new SecurityException("Acesso negado: requer ADMIN.");
        }
    }

    public void requireAdminOrSamePatio(UUID patioId) {
        if (isAdmin()) return;
        UUID userPatio = currentUserPatioIdOrNull();
        if (userPatio == null || patioId == null || !userPatio.equals(patioId)) {
            throw new SecurityException("Acesso negado: recurso de outro pátio.");
        }
    }

    public boolean isAuthenticated() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    public Optional<Usuario> currentUserOptional() {
        try {
            return Optional.of(currentUserOrThrow());
        } catch (SecurityException e) {
            return Optional.empty();
        }
    }

    public boolean isAdminAuthenticated() {
        return currentUserOptional()
                .map(u -> u.getCargo() == CargoUsuario.ADMIN)
                .orElse(false);
    }
}
