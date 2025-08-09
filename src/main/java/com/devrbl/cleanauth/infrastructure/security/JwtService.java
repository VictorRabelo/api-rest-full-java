package com.devrbl.cleanauth.infrastructure.security;

import com.devrbl.cleanauth.domain.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtService {

    private final SecretKey key;
    private final long accessMinutes;
    private final long refreshDays;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-expiration-minutes:15}") long accessMinutes,
                      @Value("${jwt.refresh-expiration-days:7}") long refreshDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }

    public String generateAccessToken(User user, UUID jti) {
        return buildToken(user, jti, accessMinutes, ChronoUnit.MINUTES);
    }

    public String generateRefreshToken(User user, UUID jti) {
        return buildToken(user, jti, refreshDays, ChronoUnit.DAYS);
    }

    private String buildToken(User user, UUID jti, long amount, ChronoUnit unit) {
        Instant now = Instant.now();
        Instant exp = now.plus(amount, unit);
        return Jwts.builder()
                .setId(jti.toString())
                .setSubject(user.getUuid())
                .claim("roles", user.getRoles())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        Claims claims = parse(token);
        return claims.getSubject().equals(((com.devrbl.cleanauth.domain.entity.User) userDetails).getUuid())
                && claims.getExpiration().after(new Date());
    }

    public Instant getExpiration(String token) {
        return parse(token).getExpiration().toInstant();
    }

    public Authentication getAuthentication(String token, UserDetails user) {
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}
