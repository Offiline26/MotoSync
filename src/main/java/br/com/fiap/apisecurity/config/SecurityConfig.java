package br.com.fiap.apisecurity.config;

import br.com.fiap.apisecurity.security.UsuarioUserDetailsService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authProvider) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authProvider);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UsuarioUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.web.filter.HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new org.springframework.web.filter.HiddenHttpMethodFilter();
    }

    @RestControllerAdvice
    public class ThymeleafErrorAdvice {

        private static final Logger log = LoggerFactory.getLogger(ThymeleafErrorAdvice.class);
        private static final Pattern EXPR =
                Pattern.compile("Exception evaluating SpringEL expression: '([^']+)'");

        @ExceptionHandler({ TemplateProcessingException.class, TemplateInputException.class })
        public ResponseEntity<Map<String, Object>> handleThymeleaf(Exception ex) {
            String template = null;
            Integer line = null, col = null;

            if (ex instanceof TemplateProcessingException tpe) {
                template = tpe.getTemplateName();
                line = tpe.getLine();
                col  = tpe.getCol();
            } else if (ex instanceof TemplateInputException tie) {
                template = tie.getTemplateName();
                line = tie.getLine();
                col  = tie.getCol();
            }

            // Tenta extrair a expressão EL que quebrou (ex.: page.pageNumber)
            String expr = extractExpression(ex);

            log.error("THYMELEAF ERROR -> template='{}' line={} col={} expr='{}' msg={}",
                    template, line, col, expr, ex.getMessage(), ex);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("template", template);
            body.put("line", line);
            body.put("col", col);
            body.put("expression", expr);
            body.put("message", ex.getMessage());

            // Opcional: devolve 500 com detalhes
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

        private static String extractExpression(Throwable ex) {
            for (Throwable t = ex; t != null; t = t.getCause()) {
                String msg = t.getMessage();
                if (msg != null) {
                    Matcher m = EXPR.matcher(msg);
                    if (m.find()) return m.group(1);
                }
            }
            return null;
        }
    }
}

