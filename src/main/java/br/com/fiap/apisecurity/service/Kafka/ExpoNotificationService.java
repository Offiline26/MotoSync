package br.com.fiap.apisecurity.service.Kafka;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExpoNotificationService {

    public void checkEmptyParkSendAlert(UUID vagaId) {
        // 1️⃣ Busca no banco quantas vagas estão ocupadas nesse pátio
        // 2️⃣ Se atingir o limite → envia push via Expo SDK
        System.out.println("🚨 Verificando ocupação de vaga " + vagaId);
        // TODO: Implementar lógica real
    }
}
