package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.UsuarioDTO;
import br.com.fiap.apisecurity.mapper.UsuarioMapper;
import br.com.fiap.apisecurity.model.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UsuarioDTO> listAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toDto)
                .collect(Collectors.toList());
    }

    public UsuarioDTO create(UsuarioDTO dto) {
        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return UsuarioMapper.toDto(usuarioRepository.save(usuario));
    }
}



