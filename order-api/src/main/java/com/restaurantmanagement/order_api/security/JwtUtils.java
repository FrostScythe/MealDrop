package com.restaurantmanagement.order_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    // Read from application.yaml — keep this secret and long (256-bit minimum)
    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration-ms:86400000}") // default: 24 hours
    private long expirationMs;

    // Lazily build the signing key from the secret string
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretString);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ─── Token Generation ──────────────────────────────────

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())   // email stored as "sub"
                .claim("roles", userDetails.getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .toList())                    // roles stored as custom claim
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
        // Result: "header.payload.signature" — a standard JWT string
    }

    // ─── Token Parsing ─────────────────────────────────────

    // Extract all claims (the "payload" part of the JWT)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // verify signature
                .build()
                .parseSignedClaims(token)
                .getPayload();
        // Throws JwtException if token is invalid or expired
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        final Date expiration = extractAllClaims(token).getExpiration();
        return email.equals(userDetails.getUsername())
                && expiration.after(new Date());
    }
}