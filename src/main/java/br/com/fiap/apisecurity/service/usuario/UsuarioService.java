package br.com.fiap.apisecurity.service.usuario;

import br.com.fiap.apisecurity.dto.usuario.RegisterRequest;
import br.com.fiap.apisecurity.dto.usuario.UsuarioPerfilResponse;
import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional
    public UUID register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase(Locale.ROOT);
        if (repo.findByEmail(email).isPresent()) {
            throw new DataIntegrityViolationException("E-mail já cadastrado");
        }
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setSenha(encoder.encode(req.getPassword()));
        u.setCargo(req.getCargo() != null ? req.getCargo() : CargoUsuario.OPERADOR_PATIO);
        u = repo.save(u);
        return u.getId();
    }

    public Usuario requireByEmail(String email) {
        return repo.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public UsuarioPerfilResponse montarPerfilParaFrontend(Usuario usuario) {
        return new UsuarioPerfilResponse(
                usuario.getId() != null ? usuario.getId().toString() : null,
                usuario.getEmail(),
                usuario.getCargo()
        );
    }
}
