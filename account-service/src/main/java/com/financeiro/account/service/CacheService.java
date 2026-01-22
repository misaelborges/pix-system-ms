package com.financeiro.account.service;

import ch.qos.logback.classic.layout.TTLLLayout;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Long CACHE_TTL = 5L;
    private static final String BALANCE_KEY_PREFIX = "balance";

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheBalance(Long accountId, BigDecimal balance) {
        String key = BALANCE_KEY_PREFIX + accountId;
        redisTemplate.opsForValue().set(key, balance.toString(), CACHE_TTL, TimeUnit.MINUTES);
    }

    public Optional<BigDecimal> getBalanceFromCache(Long accountId) {
        String key = BALANCE_KEY_PREFIX + accountId;
        Object value = redisTemplate.opsForValue().get(key);

        if (value != null) {
            return Optional.of(new BigDecimal(value.toString()));
        }

        return Optional.empty();
    }

    public void invalidateBalance(Long accountId) {
        String key = BALANCE_KEY_PREFIX + accountId;
        redisTemplate.delete(key);
    }
}
