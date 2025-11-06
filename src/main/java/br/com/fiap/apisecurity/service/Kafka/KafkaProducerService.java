package br.com.fiap.apisecurity.service.Kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC_OCUPACAO = "patio-ocupacao-eventos";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendVagaOcupadaEvent(UUID vagaId, UUID motoId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("vagaId", vagaId);
            event.put("motoId", motoId);
            event.put("timestamp", Instant.now().toString());

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_OCUPACAO, json);
            System.out.println("🚀 Evento enviado para Kafka: " + json);
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar evento Kafka: " + e.getMessage());
        }
    }
}
