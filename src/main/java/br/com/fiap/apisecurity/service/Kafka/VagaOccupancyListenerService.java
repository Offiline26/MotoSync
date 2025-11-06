package br.com.fiap.apisecurity.service.Kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VagaOccupancyListenerService {

    @Autowired
    private ExpoNotificationService notificationService;

    private static final String TOPIC_OCUPACAO = "vaga-ocupacao-eventos";
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = TOPIC_OCUPACAO, groupId = "vaga-notification-group")
    public void handleOccupancyEvent(String message) {
        System.out.println("📨 Evento Kafka recebido: " + message);
        try {
            JsonNode json = mapper.readTree(message);
            UUID vagaId = UUID.fromString(json.get("vagaId").asText());
            notificationService.checkEmptyParkSendAlert(vagaId);
        } catch (Exception e) {
            System.err.println("❌ Erro ao processar mensagem Kafka: " + e.getMessage());
        }
    }
}
