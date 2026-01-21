package com.financeiro.auth.controller;

import com.financeiro.auth.entity.dto.request.LoginRequestDTO;
import com.financeiro.auth.entity.dto.request.RefreshTokenRequestDTO;
import com.financeiro.auth.entity.dto.response.AuthResponseDTO;
import com.financeiro.auth.entity.dto.response.ValidateTokenResponseDTO;
import com.financeiro.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.login(loginRequestDTO);
        return ResponseEntity.ok().body(authResponseDTO);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.refreshToken(refreshTokenRequestDTO);
        return ResponseEntity.ok().body(authResponseDTO);
    }

    @PostMapping("/auth/validate")
    public ResponseEntity<ValidateTokenResponseDTO> validateToken(@RequestParam String token) {
        ValidateTokenResponseDTO validateTokenResponseDTO = authService.validateToken(token);
        return ResponseEntity.ok().body(validateTokenResponseDTO);
    }
}
