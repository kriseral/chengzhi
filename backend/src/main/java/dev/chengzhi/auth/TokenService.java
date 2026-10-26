package dev.chengzhi.auth;

import dev.chengzhi.config.AppProperties;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final AppProperties properties;
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public TokenService(AppProperties properties) {
        this.properties = properties;
    }

    public String issue(UserPrincipal principal) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(principal, Instant.now().plus(properties.auth().tokenTtl())));
        return token;
    }

    public Optional<Authentication> authenticate(String token) {
        Session session = sessions.get(token);
        if (session == null || session.expiresAt().isBefore(Instant.now())) {
            sessions.remove(token);
            return Optional.empty();
        }
        return Optional.of(new UsernamePasswordAuthenticationToken(
                session.principal(), session.principal().passwordHash(), session.principal().getAuthorities()));
    }

    private record Session(UserPrincipal principal, Instant expiresAt) {
    }
}
