package za.co.costcomining.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpMs;
    private final long refreshTokenExpMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms:86400000}") long accessTokenExpMs,
            @Value("${jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpMs = accessTokenExpMs;
        this.refreshTokenExpMs = refreshTokenExpMs;
    }

    public String generateAccessToken(String userId, String email, String role, String vendorId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpMs);

        var builder = Jwts.builder()
                .subject(userId)
                .claims(Map.of(
                        "email", email,
                        "role", role
                ))
                .issuedAt(now)
                .expiration(expiry);

        if (vendorId != null) {
            builder.claim("vendorId", vendorId);
        }

        return builder.signWith(key).compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpMs);

        return Jwts.builder()
                .subject(userId)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return parseToken(token).get("role", String.class);
    }

    public String getEmailFromToken(String token) {
        return parseToken(token).get("email", String.class);
    }

    public String getVendorIdFromToken(String token) {
        return parseToken(token).get("vendorId", String.class);
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpMs;
    }
}
