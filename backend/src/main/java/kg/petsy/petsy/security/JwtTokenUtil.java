package kg.petsy.petsy.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kg.petsy.petsy.model.User;
import kg.petsy.petsy.model.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenUtil {

    private final String secretKey;

    private final long accessExp;

    private final long refreshExp;

    public JwtTokenUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-expiration}") long accessExp,
            @Value("${jwt.refresh-expiration}") long refreshExp
    ) {
        this.secretKey = secretKey;
        this.accessExp = accessExp;
        this.refreshExp = refreshExp;
    }

    public String generateAccessToken(User user) {

        if(!isUserHaveCorrectEmailAndIdAndRole(user)) {
            throw new IllegalArgumentException("У пользователя нет необходимых данных для генерации токена");
        }

        String accessToken = Jwts.builder()
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExp))
                .signWith(getSigningKey())
                .compact();

        log.info("Сгенерирован публичный токен для пользователя с почтой - {}", user.getEmail());
        return accessToken;

    }

    public String generateRefreshToken(User user) {

        if(!isUserHaveCorrectEmailAndIdAndRole(user)) {
            throw new IllegalArgumentException("У пользователя нет необходимых данных для генерации токена");
        }

        String refreshToken = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExp))
                .signWith(getSigningKey())
                .compact();

        log.info("Сгенерирован приватный токен для пользователя с почтой - {}", user.getEmail());
        return refreshToken;

    }

    public String extractEmail(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build().parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean isTokenValid(String token) {

        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;

        } catch (ExpiredJwtException ex) {
            log.warn("У токена истек срок годности");
            return false;

        } catch (JwtException ex) {
            log.warn("Произошла неизвестная ошибка с токеном");
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isUserHaveCorrectEmailAndIdAndRole(User user) {

        if(user == null || user.getEmail() == null || user.getId() == null || user.getRole() == null) {
            log.info("У пользователя нет данных или по почте - {}, или по id - {}, или по роли - {} ",
                    user.getEmail(), user.getId(), user.getRole());
            return false;
        }
        return true;
    }

}
