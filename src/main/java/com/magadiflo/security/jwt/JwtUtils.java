package com.magadiflo.security.jwt;

import com.magadiflo.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);
    private String jwtSecret = "mi-clave-secreta-12345";
    //private int jwtExpirationMs = 3600000; //1h

    //Para testear
    private int jwtExpirationMs = 60000; //1 minuto

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return this.generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, this.jwtSecret)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(this.jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(this.jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT Token {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT is expired {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT is Unsupported {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty {}", e.getMessage());
        }
        return false;
    }
}
