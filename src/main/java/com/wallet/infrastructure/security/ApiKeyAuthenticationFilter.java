package com.wallet.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Filtro de autenticação via API Key.
 * Permite autenticação alternativa para serviços internos.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    
    // Em produção, isso viria de um banco de dados ou serviço de configuração
    private static final Set<String> VALID_API_KEYS = Set.of(
        "api-key-wallet-service",
        "api-key-notification-service",
        "api-key-audit-service"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // Ignorar autenticação para Swagger, Actuator e Auth endpoints
        if (requestURI.startsWith("/swagger-ui") || 
            requestURI.startsWith("/api-docs") || 
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.startsWith("/actuator") ||
            requestURI.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String apiKey = extractApiKeyFromRequest(request);
            
            if (StringUtils.hasText(apiKey) && isValidApiKey(apiKey)) {
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        "service-" + apiKey, 
                        null, 
                        List.of(new SimpleGrantedAuthority("ROLE_SERVICE"))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("API Key authentication successful for service: {}", apiKey);
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            log.error("API Key authentication failed", e);
            handleAuthenticationError(response, "Invalid API key");
        }
    }

    private String extractApiKeyFromRequest(HttpServletRequest request) {
        // Verificar no header X-API-Key
        String apiKey = request.getHeader("X-API-Key");
        
        if (StringUtils.hasText(apiKey)) {
            return apiKey;
        }
        
        // Verificar no header Authorization (formato: ApiKey <key>)
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("ApiKey ")) {
            return authorization.substring(7);
        }
        
        return null;
    }

    private boolean isValidApiKey(String apiKey) {
        return VALID_API_KEYS.contains(apiKey);
    }

    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = Map.of(
            "status", HttpStatus.UNAUTHORIZED.value(),
            "error", "Unauthorized",
            "message", message,
            "timestamp", System.currentTimeMillis()
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
} 