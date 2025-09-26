package br.com.fiap.apisecurity.service.usuario;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "app.jwt.secret")   // <-- só cria o bean se existir a propriedade
public class JwtService {

    private final Key signingKey;
    private final long ttlMillis;
    private final String issuer;
    private final long refreshTtlMillis;

    public JwtService(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.ttl-minutes:60}") long ttlMinutes,
            @Value("${app.jwt.issuer:apisecurity}") String issuer,
            @Value("${app.jwt.refresh-ttl-minutes:43200}") long refreshTtlMinutes
    ) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.ttlMillis = Duration.ofMinutes(ttlMinutes).toMillis();
        this.refreshTtlMillis = Duration.ofMinutes(refreshTtlMinutes).toMillis();
        this.issuer = issuer;
    }

    public String generateToken(UserDetails user) {
        return build(user.getUsername(), Map.of(), ttlMillis);
    }
    public String generateRefreshToken(String subject) {
        return build(subject, Map.of("typ","refresh"), refreshTtlMillis);
    }
    private String build(String sub, Map<String,Object> claims, long life) {
        Date now = new Date(), exp = new Date(now.getTime()+life);
        return Jwts.builder()
                .setClaims(claims).setSubject(sub).setIssuer(issuer)
                .setIssuedAt(now).setExpiration(exp)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
