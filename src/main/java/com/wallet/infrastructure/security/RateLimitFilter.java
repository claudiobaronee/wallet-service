package com.wallet.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting simplificado baseado em bucket token.
 * Implementa limitação de requisições por IP e por usuário.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    
    @Value("${rate-limit.ip-requests-per-minute:100}")
    private int ipRequestsPerMinute;
    
    @Value("${rate-limit.user-requests-per-minute:1000}")
    private int userRequestsPerMinute;
    
    // Cache de buckets por IP
    private final Map<String, TokenBucket> ipBuckets = new ConcurrentHashMap<>();
    
    // Cache de buckets por usuário
    private final Map<String, TokenBucket> userBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        
        // Ignorar rate limiting para Swagger e Actuator
        if (requestURI.startsWith("/swagger-ui") || 
            requestURI.startsWith("/api-docs") || 
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIp(request);
        String userId = getUserId(request);
        
        // Verificar rate limit por IP
        if (!isAllowedByIp(clientIp)) {
            handleRateLimitExceeded(response, "Rate limit exceeded for IP: " + clientIp);
            return;
        }
        
        // Verificar rate limit por usuário (se autenticado)
        if (userId != null && !isAllowedByUser(userId)) {
            handleRateLimitExceeded(response, "Rate limit exceeded for user: " + userId);
            return;
        }
        
        // Adicionar headers de rate limit
        addRateLimitHeaders(response, clientIp, userId);
        
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private String getUserId(HttpServletRequest request) {
        // Em uma implementação real, você extrairia o userId do JWT token
        // Por simplicidade, vamos usar um header customizado
        return request.getHeader("X-User-ID");
    }

    private boolean isAllowedByIp(String clientIp) {
        TokenBucket bucket = ipBuckets.computeIfAbsent(clientIp, 
            k -> new TokenBucket(ipRequestsPerMinute, ipRequestsPerMinute));
        
        return bucket.tryConsume();
    }

    private boolean isAllowedByUser(String userId) {
        TokenBucket bucket = userBuckets.computeIfAbsent(userId, 
            k -> new TokenBucket(userRequestsPerMinute, userRequestsPerMinute));
        
        return bucket.tryConsume();
    }

    private void addRateLimitHeaders(HttpServletResponse response, String clientIp, String userId) {
        TokenBucket ipBucket = ipBuckets.get(clientIp);
        if (ipBucket != null) {
            response.setHeader("X-Rate-Limit-Remaining-IP", String.valueOf(ipBucket.getAvailableTokens()));
            response.setHeader("X-Rate-Limit-Reset-IP", String.valueOf(ipBucket.getResetTime()));
        }
        
        if (userId != null) {
            TokenBucket userBucket = userBuckets.get(userId);
            if (userBucket != null) {
                response.setHeader("X-Rate-Limit-Remaining-User", String.valueOf(userBucket.getAvailableTokens()));
                response.setHeader("X-Rate-Limit-Reset-User", String.valueOf(userBucket.getResetTime()));
            }
        }
    }

    private void handleRateLimitExceeded(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = Map.of(
            "status", HttpStatus.TOO_MANY_REQUESTS.value(),
            "error", "Too Many Requests",
            "message", message,
            "timestamp", LocalDateTime.now().toString()
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        log.warn("Rate limit exceeded: {}", message);
    }

    /**
     * Implementação de bucket token para rate limiting.
     */
    private static class TokenBucket {
        private final int capacity;
        private final double refillRate; // tokens por segundo
        private double availableTokens;
        private long lastRefillTime;

        public TokenBucket(int capacity, double refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.availableTokens = capacity;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume() {
            refill();
            
            if (availableTokens >= 1) {
                availableTokens -= 1;
                return true;
            }
            
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            double timePassed = (now - lastRefillTime) / 1000.0; // converter para segundos
            double tokensToAdd = timePassed * refillRate;
            
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillTime = now;
        }

        public synchronized int getAvailableTokens() {
            refill();
            return (int) availableTokens;
        }

        public synchronized long getResetTime() {
            if (availableTokens >= 1) {
                return 0; // Não precisa resetar
            }
            
            double tokensNeeded = 1 - availableTokens;
            double secondsNeeded = tokensNeeded / refillRate;
            return (long) (secondsNeeded * 1000);
        }
    }
} 