package com.financeiro.auth.service;

import com.financeiro.auth.entity.Role;
import com.financeiro.auth.entity.User;
import com.financeiro.auth.exception.InvalidCredentialsException;
import com.financeiro.auth.exception.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpirationSeconds;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpirationSeconds;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateAccessToken(User user) {
        List<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        JwtClaimsSet auth = JwtClaimsSet.builder()
                .issuer("auth")
                .subject(user.getId().toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(accessTokenExpirationSeconds))
                .claim("email", user.getEmail())
                .claim("roles", roleNames)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(auth)).getTokenValue();
    }

    public String generateRefreshToken(User user) {
        JwtClaimsSet refreshClaims  = JwtClaimsSet.builder()
                .issuer("auth")
                .subject(user.getId().toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(refreshTokenExpirationSeconds))
                .claim("type", "refresh")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();
    }

    public Long getUserIdFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(Instant.now())) {
                throw new TokenExpiredException("Token expirado");
            }
            return Long.parseLong(jwt.getSubject());
        } catch (TokenExpiredException e) {
            throw e;
        } catch (JwtException e) {
            throw new InvalidCredentialsException("Token inválido ou não pode ser processado");
        }
    }

    public List<String> getRolesFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(Instant.now())) {
                throw new TokenExpiredException("Token expirado");
            }
            List<?> roles = jwt.getClaimAsStringList("roles");
            return roles != null ? roles.stream()
                    .map(Object::toString)
                    .toList() : List.of();
        } catch (TokenExpiredException e) {
            throw e;
        } catch (JwtException e) {
            throw new InvalidCredentialsException("Token inválido ou não pode ser processado");
        }
    }
}
