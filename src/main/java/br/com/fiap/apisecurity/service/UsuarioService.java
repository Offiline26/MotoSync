package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.dto.UsuarioDTO;
import br.com.fiap.apisecurity.mapper.UsuarioMapper;
import br.com.fiap.apisecurity.model.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Create
    @Transactional
    @CachePut(value = "usuarios", key = "#result.id")
    public UsuarioDTO createUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = UsuarioMapper.toEntity(usuarioDTO);
        return UsuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    // Read by ID
    @Cacheable(value = "usuarios", key = "#id")
    public UsuarioDTO readUsuarioById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        return UsuarioMapper.toDto(usuario);
    }

    // Read by email
    public Optional<UsuarioDTO> readByEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.map(UsuarioMapper::toDto);
    }

    // Read by CPF
    public Optional<UsuarioDTO> readByCpf(String cpf) {
        Optional<Usuario> usuario = usuarioRepository.findByCpf(cpf);
        return usuario.map(UsuarioMapper::toDto);
    }

    // Read all
    @Cacheable(value = "usuarios", key = "#pageable")
    public Page<UsuarioDTO> readAllUsuarios(Pageable pageable) {
        Page<Usuario> page = usuarioRepository.findAll(pageable);
        List<UsuarioDTO> dtoList = page.getContent()
                .stream()
                .map(UsuarioMapper::toDto)
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }

    // Update
    @Transactional
    @CachePut(value = "usuarios", key = "#result.id")
    public UsuarioDTO updateUsuario(UUID id, UsuarioDTO usuarioDTO) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isEmpty()) {
            return null;
        }
        Usuario usuario = UsuarioMapper.toEntity(usuarioDTO);
        usuario.setId(id);
        return UsuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    // Delete
    @Transactional
    @CacheEvict(value = "usuarios", key = "#id")
    public void deleteUsuario(UUID id) {
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioDTO> readByNome(String nome) {
        List<Usuario> usuarios = usuarioRepository.findByNomeContainingIgnoreCase(nome);
        return usuarios.stream()
                .map(UsuarioMapper::toDto)
                .toList();
    }
}


