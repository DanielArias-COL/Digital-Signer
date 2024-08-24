package com.digital.signer.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getEncoded();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(Integer idUser) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, idUser);
    }

    private String createToken(Map<String, Object> claims, Integer subject) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(subject)) // sub: Identifica al sujeto del token
                .setIssuer("http://localhost:9078") // iss: Identifica al emisor del token
                .setAudience("http://localhost:4200") // aud: Identifica al destinatario del token
                .setIssuedAt(new Date(currentTimeMillis)) // iat: Fecha y hora en que se emitió el token
                .setNotBefore(new Date(currentTimeMillis)) // nbf: Momento a partir del cual el token es válido
                .setExpiration(new Date(currentTimeMillis + 1000 * 60 * 60 * 10)) // exp: Establece el tiempo de expiración (10 horas)
                .setId(UUID.randomUUID().toString()) // jti: Identificador único para este JWT
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firmado con el algoritmo HS256
                .compact();
    }

    public boolean validateToken(String token) {
        return (extractUserId(token) != null && !isTokenExpired(token));
    }
}
