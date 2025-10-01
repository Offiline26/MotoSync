package br.com.fiap.apisecurity.service.usuario;

import br.com.fiap.apisecurity.dto.usuario.RegisterRequest;
import br.com.fiap.apisecurity.dto.usuario.UsuarioPerfilResponse;
import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.service.PatioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final PatioService patioService; // <- novo

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder, PatioService patioService) {
        this.repo = repo;
        this.encoder = encoder;
        this.patioService = patioService;
    }

    @Transactional
    public UUID register(RegisterRequest req) {
        final String email = req.getEmail().trim().toLowerCase(Locale.ROOT);

        if (repo.findByEmail(email).isPresent()) {
            throw new DataIntegrityViolationException("E-mail já cadastrado");
        }

        final CargoUsuario cargo = Optional.ofNullable(req.getCargo())
                .orElse(CargoUsuario.OPERADOR_PATIO);

        final Usuario u = new Usuario();
        u.setEmail(email);
        u.setSenha(encoder.encode(req.getPassword()));
        u.setCargo(cargo);

        if (cargo == CargoUsuario.OPERADOR_PATIO) {
            UUID patioId = req.getPatioId(); // agora UUID
            if (patioId == null) {
                throw new IllegalArgumentException("Selecione o pátio onde o operador atua.");
            }
            var patio = patioService.readPatioEntityById(patioId)
                    .orElseThrow(() -> new IllegalArgumentException("Pátio não encontrado."));
            u.setPatio(patio);
        } else {
            u.setPatio(null);
        }

        return repo.save(u).getId();
    }

    public Usuario requireByEmail(String email) {
        return repo.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public UsuarioPerfilResponse montarPerfilParaFrontend(Usuario usuario) {
        var patio = usuario.getPatio();
        return new UsuarioPerfilResponse(
                usuario.getId() != null ? usuario.getId().toString() : null,
                usuario.getEmail(),
                usuario.getCargo(),
                patio != null ? patio.getId() : null,
                patio != null ? patio.getNome() : null
        );
    }

    // --- helpers ---
    private UUID parseUuid(String raw) {
        try {
            return UUID.fromString(raw.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Identificador de pátio inválido.");
        }
    }

}
