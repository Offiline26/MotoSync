package br.com.fiap.apisecurity.security;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioUserDetailsService uds;

    public SecurityConfig(UsuarioUserDetailsService uds) { this.uds = uds; }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean AuthenticationProvider authenticationProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Ignora CSRF para URLs de API, mas mantém CSRF para outras URLs
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso às páginas de login, registro, recursos estáticos e URLs de callback do OAuth2
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/login/oauth2/code/google", "/login/oauth2/code/github").permitAll()
                        .anyRequest().authenticated() // Requer autenticação para outras páginas
                )
                // Configuração de login com formulário
                .formLogin(f -> f
                        .loginPage("/login") // Página de login personalizada
                        .usernameParameter("email")  // Nome do campo no formulário para o e-mail
                        .passwordParameter("password")  // Nome do campo no formulário para a senha
                        .defaultSuccessUrl("/", true)  // Redireciona para a home após login
                        .permitAll()  // Permite acesso à página de login sem autenticação
                )
                // Configuração de logout
                .logout(l -> l.logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")  // Redireciona para a página de login após logout
                        .permitAll()
                )
                // Configuração de autenticação OAuth2 (habilita login com Google, GitHub, etc.)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // Página de login personalizada (opcional)
                        .defaultSuccessUrl("/", true)  // Redireciona para a home após login
                )
                // Adiciona o provider de autenticação (se necessário)
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    // >>> este bean é o que você precisa injetar no controller <<<
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
