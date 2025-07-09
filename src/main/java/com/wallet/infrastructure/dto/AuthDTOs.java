package com.wallet.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "username é obrigatório")
        private String username;
        
        @NotBlank(message = "password é obrigatório")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String message;
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private String username;
        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshRequest {
        @NotBlank(message = "refreshToken é obrigatório")
        private String refreshToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshResponse {
        private String message;
        private String accessToken;
        private String tokenType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateResponse {
        private String message;
        private boolean valid;
        private String username;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "username é obrigatório")
        private String username;
        
        @NotBlank(message = "password é obrigatório")
        private String password;
        
        private String role = "USER";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterResponse {
        private String message;
        private String username;
        private String role;
    }
} 