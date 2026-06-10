package com.restaurant.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.time.Instant; import java.util.Date; import java.util.List;
@Service
public class JwtService {
 @Value("${app.jwt.secret}") private String secret; @Value("${app.jwt.expiration-minutes}") private long expirationMinutes;
 private SecretKey key(){ return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
 public String generateToken(SecurityUser user){ List<String> roles=user.getAuthorities().stream().map(Object::toString).toList(); Instant now=Instant.now(); return Jwts.builder().subject(user.getUsername()).claim("userId", user.getUserId()).claim("roles", roles).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expirationMinutes*60))).signWith(key()).compact(); }
 public String extractEmail(String token){ return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject(); }
 public boolean isValid(String token, SecurityUser user){ return extractEmail(token).equals(user.getUsername()); }
}
