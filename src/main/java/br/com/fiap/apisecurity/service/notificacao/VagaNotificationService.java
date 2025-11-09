package br.com.fiap.apisecurity.service.notificacao;

import br.com.fiap.apisecurity.model.Moto;
import br.com.fiap.apisecurity.model.Vaga;
import org.springframework.stereotype.Service;

@Service
public class VagaNotificationService {

    private final ExpoNotificationService expoNotificationService;

    public VagaNotificationService(ExpoNotificationService expoNotificationService) {
        this.expoNotificationService = expoNotificationService;
    }

    public void notificarOcupacao(Vaga vaga, Moto moto) {
        String titulo = "Vaga ocupada";
        String mensagem = String.format(
                "A moto %s ocupou a vaga %s no pátio %s.",
                moto.getPlaca(),
                vaga.getIdentificacao(),           // ajuste conforme seu campo
                vaga.getPatio().getNome()
        );

        // Aqui você chama o mesmo método que o listener chamava
        expoNotificationService.enviarNotificacaoParaPatio(
                vaga.getPatio(),
                titulo,
                mensagem
        );
    }

    public void notificarDesocupacao(Vaga vaga, Moto moto) {
        String titulo = "Vaga liberada";
        String mensagem = String.format(
                "A moto %s saiu da vaga %s no pátio %s.",
                moto.getPlaca(),
                vaga.getIdentificacao(),
                vaga.getPatio().getNome()
        );

        expoNotificationService.enviarNotificacaoParaPatio(
                vaga.getPatio(),
                titulo,
                mensagem
        );
    }
}
