package com.financeiro.auth.service;

import com.financeiro.auth.entity.User;
import com.financeiro.auth.entity.dto.request.LoginRequestDTO;
import com.financeiro.auth.entity.dto.request.RefreshTokenRequestDTO;
import com.financeiro.auth.entity.dto.response.AuthResponseDTO;
import com.financeiro.auth.entity.dto.response.ValidateTokenResponseDTO;
import com.financeiro.auth.exception.InvalidCredentialsException;
import com.financeiro.auth.exception.TokenExpiredException;
import com.financeiro.auth.exception.UserNotFoundException;
import com.financeiro.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpirationSeconds;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.email()).orElseThrow(
                () -> new UserNotFoundException("Usuário não encontrado"));

        if (!bCryptPasswordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Email ou senha inválidos");
        }

        String acessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponseDTO(acessToken, refreshToken, accessTokenExpirationSeconds, user.getId());
    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        Long userId = jwtService.getUserIdFromToken(refreshTokenRequestDTO.refreshToken());

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        String acessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponseDTO(acessToken, refreshToken, accessTokenExpirationSeconds, user.getId());
    }

    public ValidateTokenResponseDTO validateToken(String token) {
        try {
            Long userId = jwtService.getUserIdFromToken(token);
            List<String> roles = jwtService.getRolesFromToken(token);

            userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

            return new ValidateTokenResponseDTO(true, userId, roles);
        } catch (TokenExpiredException | InvalidCredentialsException | UserNotFoundException e) {
            return new ValidateTokenResponseDTO(false, null, List.of());
        }
    }

}
