package br.com.fiap.apisecurity.security;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HibernateNoDdlConfig implements HibernatePropertiesCustomizer {
    @Override
    public void customize(Map<String, Object> props) {
        // NÃO mexe no schema: só valida
        props.put("hibernate.hbm2ddl.auto", "validate");
        // Desliga qualquer geração de schema JPA
        props.put("jakarta.persistence.schema-generation.database.action", "none");
    }
}
