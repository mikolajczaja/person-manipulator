package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
public class RedisTemplateConfiguration {

    @Value("${spring.data.redis.host:localhost}")
    private String hostname;
    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Bean
    LettuceConnectionFactory connectionFactory() {
        log.info("Connecting to Redis {}:{}", hostname, port);
        return new LettuceConnectionFactory(hostname, port);
    }

    @Bean
    RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
