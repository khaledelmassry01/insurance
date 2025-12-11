package com.insurance.security;

import com.insurance.model.Policyholder;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides functionality for generating and validating JWT tokens.
 */
@Component
public class JwtTokenProvider {

    // Secret key for signing JWT tokens
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long jwtExpirationInMs = 86_400_000; // 24 hours

    /**
     * Generates a new JWT token with the agent ID in the subject field.
     */
    public String generateToken(Authentication authentication) {
        // Get agent details from the authentication object
        AgentUserDetails userDetails = (AgentUserDetails) authentication.getPrincipal();

        // Collect authorities and prefix with ROLE_ if necessary
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth : "ROLE_" + auth)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Set agent ID as the subject
        String subject = String.valueOf(userDetails.getAgentId());

        return Jwts.builder()
                .setSubject(subject)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the agent ID from a JWT token.
     */
    public int getAgentIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Integer.parseInt(claims.getSubject());
    }

    /**
     * Extracts the list of roles from a JWT token.
     */
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String rolesString = claims.get("roles", String.class);
        if (rolesString != null) {
            return List.of(rolesString.split(","));
        } else {
            return List.of();
        }
    }

    /**
     * Validates a JWT token by checking its signature and expiration date.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Extracts the email from a policyholder JWT token.
     *
     * @param token The JWT token
     * @return The email address from the token, or null if not a valid policyholder token or email is missing.
     */
    public String getEmailFromPolicyholderToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if ("policyholder".equals(claims.get("type", String.class))) {
                return claims.get("email", String.class);
            }
            // Optionally, log or throw an exception if the type is not 'policyholder'
            // or if the token was expected to be a policyholder token at this stage.
            // logger.warn("Attempted to get email from a non-policyholder token or token missing 'type' claim.");
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            // logger.error("Error parsing token to get policyholder email: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generates a JWT token for a policyholder.
     * 
     * @param policyholder The policyholder entity
     * @return A JWT token string
     */
    public String generatePolicyholderToken(Policyholder policyholder) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        
        // Set policyholder ID as the subject with a prefix to distinguish from agent tokens
        String subject = "PH_" + policyholder.getPolicyholderId();
        
        return Jwts.builder()
                .setSubject(subject)
                .claim("email", policyholder.getEmail())
                .claim("type", "policyholder")
                .claim("roles", "ROLE_POLICYHOLDER")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * Extracts the policyholder ID from a JWT token.
     * 
     * @param token The JWT token
     * @return The policyholder ID
     * @throws IllegalArgumentException if the token is not a valid policyholder token
     */
    public int getPolicyholderIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            String subject = claims.getSubject();
            if (subject != null && subject.startsWith("PH_")) {
                try {
                    return Integer.parseInt(subject.substring(3));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid policyholder ID format in token", e);
                }
            }
            throw new IllegalArgumentException("Token is not a policyholder token");
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
    
    /**
     * Checks if a token is a policyholder token.
     * 
     * @param token The JWT token
     * @return True if it's a policyholder token, false otherwise
     */
    public boolean isPolicyholderToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            String type = claims.get("type", String.class);
            return "policyholder".equals(type);
        } catch (Exception e) {
            return false;
        }
    }
}
