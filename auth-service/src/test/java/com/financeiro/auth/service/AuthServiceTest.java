package com.financeiro.auth.service;

import com.financeiro.auth.entity.Role;
import com.financeiro.auth.entity.User;
import com.financeiro.auth.entity.dto.request.LoginRequestDTO;
import com.financeiro.auth.entity.dto.request.RefreshTokenRequestDTO;
import com.financeiro.auth.entity.dto.response.AuthResponseDTO;
import com.financeiro.auth.entity.dto.response.ValidateTokenResponseDTO;
import com.financeiro.auth.exception.InvalidCredentialsException;
import com.financeiro.auth.exception.UserNotFoundException;
import com.financeiro.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;
    private AuthResponseDTO authResponseDTO;
    private ValidateTokenResponseDTO validateTokenResponseDTO;
    private LoginRequestDTO loginRequestDTO;
    private RefreshTokenRequestDTO refreshTokenRequestDTO;
    private List<String> listRoles = List.of("BASIC", "ADMIN");
    private Set<Role> roles = Set.of(new Role(1L, "Admin"));


    @BeforeEach
    void setup() {
        user = new User(1L, "teste@exemple.com", "senhaTeste123@", roles, OffsetDateTime.now(), OffsetDateTime.now().plusMinutes(300L));
        authResponseDTO = new AuthResponseDTO("access-token", "refresh-token", 3600L, 1L);
        validateTokenResponseDTO = new ValidateTokenResponseDTO(true, 1L, listRoles);
        loginRequestDTO = new LoginRequestDTO("teste@example.com", "senhaTeste123@");
        refreshTokenRequestDTO = new RefreshTokenRequestDTO("refresh-token");
    }

    @Test
    @DisplayName("Deve realizar login com sucesso quando email e senha forem válidos")
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(loginRequestDTO.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        AuthResponseDTO result = authService.login(loginRequestDTO);

        assertNotNull(result);
        assertEquals("access-token", result.acessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertEquals(user.getId(), result.userId());

        verify(userRepository, times(1)).findByEmail(loginRequestDTO.email());
        verify(bCryptPasswordEncoder, times(1)).matches(loginRequestDTO.password(), user.getPassword());
        verify(jwtService, times(1)).generateAccessToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não for encontrado no login")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
           authService.login(loginRequestDTO);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha informada for inválida")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        when(userRepository.findByEmail(loginRequestDTO.email())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(loginRequestDTO.password(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> {
           authService.login(loginRequestDTO);
        });
    }

    @Test
    @DisplayName("Deve gerar novos tokens com sucesso a partir de um refresh token válido")
    void shouldRefreshTokenSuccessfully() {
        when(jwtService.getUserIdFromToken(refreshTokenRequestDTO.refreshToken())).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");

        AuthResponseDTO result = authService.refreshToken(refreshTokenRequestDTO);

        assertNotNull(result);
        assertEquals("access-token", result.acessToken());
        assertEquals("new-refresh-token", result.refreshToken());
        assertEquals(user.getId(), result.userId());

        verify(jwtService, times(1)).getUserIdFromToken(refreshTokenRequestDTO.refreshToken());
        verify(userRepository, times(1)).findById(1L);
        verify(jwtService, times(1)).generateAccessToken(user);
        verify(jwtService, times(1)).generateRefreshToken(user);


    }
}