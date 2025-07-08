package com.wallet.infrastructure.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuração de cache Redis para o Wallet Service.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(ObjectMapper objectMapper) {
        return builder -> {
            // Configuração padrão para todos os caches
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // TTL padrão de 30 minutos
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(createJsonSerializer(objectMapper)));
            
            builder.withCacheConfiguration("wallets", defaultConfig.entryTtl(Duration.ofMinutes(15)));
            builder.withCacheConfiguration("transactions", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        };
    }

    private GenericJackson2JsonRedisSerializer createJsonSerializer(ObjectMapper objectMapper) {
        ObjectMapper cacheObjectMapper = objectMapper.copy();
        cacheObjectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        
        return new GenericJackson2JsonRedisSerializer(cacheObjectMapper);
    }
} 