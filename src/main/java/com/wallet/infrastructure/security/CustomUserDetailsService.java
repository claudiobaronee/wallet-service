package com.wallet.infrastructure.security;

import jakarta.annotation.PostConstruct;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            
            createUser("admin", passwordEncoder.encode("admin123"), "ADMIN");
            createUser("user1", passwordEncoder.encode("user123"), "USER");
            createUser("user2", passwordEncoder.encode("user456"), "USER");
            
            System.out.println("CustomUserDetailsService inicializado com sucesso");
        } catch (Exception e) {
            System.err.println("Erro ao inicializar CustomUserDetailsService: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createUser(String username, String encodedPassword, String role) {
        UserDetails user = User.builder()
                .username(username)
                .password(encodedPassword)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
        
        users.put(username, user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        return user;
    }

    public void createNewUser(String username, String password, String role) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        createUser(username, passwordEncoder.encode(password), role);
    }
} 