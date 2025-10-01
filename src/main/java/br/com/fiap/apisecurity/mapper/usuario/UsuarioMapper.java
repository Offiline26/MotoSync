package br.com.fiap.apisecurity.mapper.usuario;

import br.com.fiap.apisecurity.dto.usuario.RegisterRequest;
import br.com.fiap.apisecurity.dto.usuario.UpdateUsuarioDTO;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario fromRegister(RegisterRequest req) {
        Usuario u = new Usuario();
        u.setEmail(req.getEmail());
        u.setSenha(req.getPassword()); // o Service codifica (BCrypt)
        u.setCargo(req.getCargo());
        return u;
    }
}
