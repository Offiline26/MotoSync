package br.com.fiap.apisecurity.mapper.usuario;

import br.com.fiap.apisecurity.dto.usuario.UsuarioDTO;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {


    public Usuario toEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setNome(dto.getNome());
        u.setNomeUsuario(dto.getNomeUsuario());
        u.setEmail(dto.getEmail());
        u.setSenha(dto.getSenha()); // será codificada no service
        u.setCargo(dto.getCargo()); // <-- enum direto
        return u;
    }

    public UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome(u.getNome());
        dto.setNomeUsuario(u.getNomeUsuario());
        dto.setEmail(u.getEmail());
        dto.setCargo(u.getCargo()); // <-- enum direto
        // nunca exponha a senha no DTO de saída
        return dto;
    }
}
