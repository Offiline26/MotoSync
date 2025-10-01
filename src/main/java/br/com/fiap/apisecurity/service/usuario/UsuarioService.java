package br.com.fiap.apisecurity.service.usuario;

import br.com.fiap.apisecurity.controller.usuario.Authz;
import br.com.fiap.apisecurity.dto.PatioDTO;
import br.com.fiap.apisecurity.dto.usuario.RegisterRequest;
import br.com.fiap.apisecurity.dto.usuario.UsuarioPerfilResponse;
import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.enums.CargoUsuario;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.service.PatioService;
import jakarta.persistence.EntityNotFoundException;
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
    private final PatioService patioService;
    private final Authz authz;

    public UsuarioService(UsuarioRepository repo,
                          PasswordEncoder encoder,
                          PatioService patioService,
                          Authz authz) {
        this.repo = repo;
        this.encoder = encoder;
        this.patioService = patioService;
        this.authz = authz;
    }

    @Transactional
    public UUID register(RegisterRequest req) {
        if (req == null || req.getEmail() == null || req.getPassword() == null) {
            throw new IllegalArgumentException("Dados de registro inválidos.");
        }

        final String email = req.getEmail().trim().toLowerCase(Locale.ROOT);
        if (repo.findByEmail(email).isPresent()) {
            throw new DataIntegrityViolationException("E-mail já cadastrado");
        }

        // Safe-check: se não estiver autenticado, tratamos como não-admin
        boolean adminAutenticado;
        try {
            adminAutenticado = authz.isAdmin();
        } catch (SecurityException ex) {
            adminAutenticado = false;
        }

        // Se não for ADMIN logado, força OPERADOR_PATIO
        final CargoUsuario cargo = adminAutenticado
                ? Optional.ofNullable(req.getCargo()).orElse(CargoUsuario.OPERADOR_PATIO)
                : CargoUsuario.OPERADOR_PATIO;

        final Usuario u = new Usuario();
        u.setEmail(email);
        u.setSenha(encoder.encode(req.getPassword()));
        u.setCargo(cargo);

        if (cargo == CargoUsuario.OPERADOR_PATIO) {
            UUID patioId = req.getPatioId();
            if (patioId == null) {
                throw new IllegalArgumentException("Selecione o pátio onde o operador atua.");
            }

            // NÃO chame readPatioById aqui (exige autenticação).
            // Valide existência carregando a ENTIDADE diretamente:
            Patio patio = patioService.findById(patioId)
                    .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + patioId));

            u.setPatio(patio);
        } else {
            // ADMIN normalmente não fica preso a um pátio
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
