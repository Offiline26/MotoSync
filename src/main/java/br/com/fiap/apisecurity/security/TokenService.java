package br.com.fiap.apisecurity.security;

import br.com.fiap.apisecurity.model.Usuario;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class TokenService {

    private static final String FAKE_TOKEN = "123456";

    public String generateToken(Usuario usuario) {
        return FAKE_TOKEN;
    }

    public boolean validate(String token) {
        return FAKE_TOKEN.equals(token);
    }
}

