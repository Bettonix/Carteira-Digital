package com.example.carteiradigital.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitingService {

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimitingService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean podeRealizarTransacao(String userId) {
        String chave = "rate-limit-transaction:" + userId;

        Long tentativas = redisTemplate.opsForValue().increment(chave);
        tentativas = (tentativas != null) ? tentativas : 1L;

        if (tentativas == 1L) {
            redisTemplate.expire(chave, 1, TimeUnit.MINUTES);
        }


        return tentativas <= 5;
    }

    public boolean podeRealizarPix(String userId) {
        String chave = "rate-limit-pix:" + userId;

        Long tentativas = redisTemplate.opsForValue().increment(chave);
        tentativas = (tentativas != null) ? tentativas : 1L;

        if (tentativas == 1L) {
            redisTemplate.expire(chave, 1, TimeUnit.MINUTES);
        }

        return tentativas <= 5;
    }
}
