package com.example.course.security.authen;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.course.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@Component
public class JwtService {

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;

    public JwtService(JwtProperties jwtProperties, UserDetailsService userDetailsService) {
        this.jwtProperties = jwtProperties;
        this.userDetailsService = userDetailsService;
    }

    /* ===================== GENERATE ===================== */

    public String generateAccessToken(Authentication auth) {
        User user = (User) auth.getPrincipal();

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole().getName())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusMillis(jwtProperties.getAccessExpiresAt()))
                .sign(Algorithm.HMAC512(jwtProperties.getAccessSecret()));
    }

    public String generateRefreshToken(Authentication auth) {
        User user = (User) auth.getPrincipal();

        return JWT.create()
                .withSubject(user.getUsername())
                .withClaim("id", user.getId())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusMillis(jwtProperties.getRefreshExpiresAt()))
                .sign(Algorithm.HMAC512(jwtProperties.getRefreshSecret()));
    }

    /* ===================== VALIDATE ===================== */

    public boolean validateAccessToken(String token) {
        try {
            log.debug("Validating access token {}", token);
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(jwtProperties.getAccessSecret()))
                    .withClaimPresence("id")
                    .withClaimPresence("role")
                    .build()
                    .verify(token);

            String username = jwt.getSubject();
            User user = (User) userDetailsService.loadUserByUsername(username);

            if (!user.isEnabled()) return false;

            String tokenRole = jwt.getClaim("role").asString();
            return Objects.equals(user.getRole().getName(), tokenRole);

        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(jwtProperties.getRefreshSecret()))
                    .withClaimPresence("id")
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* ===================== EXTRACT ===================== */

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(jwtProperties.getAuthHeader());

        if (authHeader == null) return null;

        if (!authHeader.startsWith(jwtProperties.getPrefix() + " ")) return null;

        return authHeader.substring((jwtProperties.getPrefix() + " ").length()).trim();
    }


    public Long extractUserId(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("id").asLong();
    }

    public String extractRole(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("role").asString();
    }

    /* ===================== AUTH ===================== */

    public Authentication createAuthentication(String token) {
        DecodedJWT jwt = JWT.decode(token);

        String username = jwt.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
