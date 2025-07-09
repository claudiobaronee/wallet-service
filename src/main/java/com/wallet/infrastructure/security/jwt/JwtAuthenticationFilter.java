package com.wallet.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Verificar se o header Authorization existe e começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extrair o token JWT (remover "Bearer " do início)
            jwt = authHeader.substring(7);
            
            // Extrair o username do token
            username = jwtService.extractUsername(jwt);

            // Se temos um username e não há autenticação atual
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Carregar os detalhes do usuário
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                
                // Verificar se o token é válido
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    
                    // Criar o token de autenticação
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Definir os detalhes da autenticação
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Definir a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log do erro (em produção, usar logger apropriado)
            logger.error("Erro ao processar token JWT: " + e.getMessage());
        }

        // Continuar com o filtro
        filterChain.doFilter(request, response);
    }
} 