package com.wallet.infrastructure.controllers;

import com.wallet.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        log.info("Login attempt received for user: {}", request.get("username"));
        
        String username = request.get("username");
        String password = request.get("password");
        
        // Para desenvolvimento, aceita qualquer usuário/senha
        if (username != null && password != null) {
            String token = jwtService.generateToken(username);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("message", "Login successful");
            log.info("Login successful for user: {}", username);
            return ResponseEntity.ok(response);
        }
        
        log.warn("Login failed - missing username or password");
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Auth test endpoint called");
        return ResponseEntity.ok("Auth endpoint working!");
    }
} 