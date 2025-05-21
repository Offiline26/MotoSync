package br.com.fiap.apisecurity.mapper;

import br.com.fiap.apisecurity.dto.UsuarioDTO;
import br.com.fiap.apisecurity.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public final class UsuarioMapper {

    private UsuarioMapper() {}

    // Converte de Entidade para DTO
    public static Usuario toEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setCargo(dto.getCargo());
        return usuario;
    }

    public static UsuarioDTO toDto(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setEmail(usuario.getEmail());
        dto.setSenha(usuario.getSenha());
        dto.setCargo(usuario.getCargo());
        return dto;
    }
}


