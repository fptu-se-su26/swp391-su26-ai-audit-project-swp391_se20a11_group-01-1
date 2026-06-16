package com.restaurant.management.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret:defaultSecretForDevOnly}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-in-ms:86400000}")
    private long jwtExpirationInMs;

    public String generateToken(String username) {
        // TODO: Implement actual JWT generation
        throw new UnsupportedOperationException("Not implemented yet: generateToken");
    }
    
    public boolean validateToken(String token) {
        // TODO: Implement actual JWT validation
        throw new UnsupportedOperationException("Not implemented yet: validateToken");
    }
    
    public String getUsername(String token) {
        // TODO: Implement extraction of username from token
        throw new UnsupportedOperationException("Not implemented yet: getUsername");
    }
}
