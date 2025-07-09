package com.wallet.infrastructure.controllers;

import com.wallet.infrastructure.dto.AuthDTOs;
import com.wallet.infrastructure.security.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthDTOs.LoginResponse> login(@Valid @RequestBody AuthDTOs.LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            AuthDTOs.LoginResponse response = new AuthDTOs.LoginResponse(
                "Login realizado com sucesso",
                accessToken,
                refreshToken,
                "Bearer",
                userDetails.getUsername(),
                userDetails.getAuthorities().stream().findFirst().map(Object::toString).orElse("USER")
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                new AuthDTOs.LoginResponse(
                    "Credenciais inválidas",
                    null,
                    null,
                    null,
                    null,
                    null
                )
            );
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDTOs.RefreshResponse> refresh(@Valid @RequestBody AuthDTOs.RefreshRequest request) {
        try {
            String username = jwtService.extractUsername(request.getRefreshToken());
            
            if (jwtService.isTokenExpired(request.getRefreshToken())) {
                return ResponseEntity.status(401).body(
                    new AuthDTOs.RefreshResponse("Refresh token expirado", null, null)
                );
            }

            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password("")
                .authorities(Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")))
                .build();
            String newAccessToken = jwtService.generateToken(userDetails);

            AuthDTOs.RefreshResponse response = new AuthDTOs.RefreshResponse(
                "Token renovado com sucesso",
                newAccessToken,
                "Bearer"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                new AuthDTOs.RefreshResponse("Refresh token inválido", null, null)
            );
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<AuthDTOs.ValidateResponse> validate(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(
                    new AuthDTOs.ValidateResponse("Token não fornecido", false, null)
                );
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (jwtService.isTokenExpired(token)) {
                return ResponseEntity.status(401).body(
                    new AuthDTOs.ValidateResponse("Token expirado", false, null)
                );
            }

            AuthDTOs.ValidateResponse response = new AuthDTOs.ValidateResponse(
                "Token válido",
                true,
                username
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                new AuthDTOs.ValidateResponse("Token inválido", false, null)
            );
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "auth-service");
        response.put("message", "Serviço de autenticação funcionando");
        return ResponseEntity.ok(response);
    }
} 