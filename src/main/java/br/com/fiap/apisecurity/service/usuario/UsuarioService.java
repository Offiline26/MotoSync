package br.com.fiap.apisecurity.service.usuario;

import br.com.fiap.apisecurity.dto.usuario.UsuarioDTO;
import br.com.fiap.apisecurity.dto.usuario.UsuarioPerfilResponse;
import br.com.fiap.apisecurity.mapper.usuario.UsuarioMapper;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario salvar(UsuarioDTO dto) {
        Usuario usuario = mapper.toEntity(dto);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioPerfilResponse montarPerfilParaFrontend(Usuario usuario) {
        UsuarioPerfilResponse perfil = new UsuarioPerfilResponse();
        perfil.setNome_usuario(usuario.getNomeUsuario());
        perfil.setCargo(usuario.getCargo()); // enum direto
        return perfil;
    }
}
