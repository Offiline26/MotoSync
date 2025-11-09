package br.com.fiap.apisecurity.service.notificacao;

import br.com.fiap.apisecurity.model.Patio;
import br.com.fiap.apisecurity.model.enums.StatusVaga;
import br.com.fiap.apisecurity.model.usuarios.Usuario;
import br.com.fiap.apisecurity.repository.PatioRepository;
import br.com.fiap.apisecurity.repository.UsuarioRepository;
import br.com.fiap.apisecurity.repository.VagaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ExpoNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ExpoNotificationService.class);
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate;
    private final UsuarioRepository usuarioRepository;
    private final VagaRepository vagaRepository;
    private final PatioRepository patioRepository;

    public ExpoNotificationService(UsuarioRepository usuarioRepository,
                                   RestTemplateBuilder restTemplateBuilder,
                                   VagaRepository vagaRepository,
                                   PatioRepository patioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.restTemplate = restTemplateBuilder.build();
        this.vagaRepository = vagaRepository;
        this.patioRepository = patioRepository;
    }

    /**
     * Envia notificação para todos os usuários de um pátio
     */
    public void enviarNotificacaoParaPatio(Patio patio, String titulo, String mensagem) {
        List<Usuario> usuarios = usuarioRepository.findAllByPatioId(patio.getId());

        usuarios.stream()
                .map(Usuario::getExpoPushToken)
                .filter(Objects::nonNull)
                .filter(token -> !token.isBlank())
                .forEach(token -> {
                    try {
                        enviarNotificacao(token, titulo, mensagem);
                    } catch (Exception e) {
                        log.error("Erro ao enviar notificação para token {}: {}", token, e.getMessage());
                    }
                });
    }

    /**
     * Envia uma notificação para um único token Expo
     */
    public void enviarNotificacao(String expoPushToken, String titulo, String corpo) {
        Map<String, Object> body = new HashMap<>();
        body.put("to", expoPushToken);
        body.put("title", titulo);
        body.put("body", corpo);
        body.put("sound", "default");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        var response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);
        log.info("Notificação enviada para {}. Status: {}", expoPushToken, response.getStatusCode());
    }

    /**
     * Exemplo de verificação de ocupação e envio de alerta.
     * Aqui você pode chamar esse método via agendamento, endpoint, etc.
     */
    public void checkEmptyParkSendAlert(UUID patioId) {
        log.info("🔎 Verificando ocupação do pátio {}", patioId);

        long vagasLivres = vagaRepository.countByPatio_IdAndStatus(patioId, StatusVaga.LIVRE);

        Patio patio = patioRepository.findById(patioId)
                .orElseThrow(() -> new EntityNotFoundException("Pátio não encontrado: " + patioId));

        // se não houver vagas livres, dispara notificação
        if (vagasLivres == 0) {
            enviarNotificacaoParaPatio(
                    patio,
                    "Pátio lotado",
                    "Não há mais vagas disponíveis no pátio " + patio.getNome()
            );
        }
    }
}